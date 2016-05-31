package org.auscope.portal.core.services.cloud;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.auscope.portal.core.cloud.CloudJob;
import org.auscope.portal.core.services.PortalServiceException;
import org.auscope.portal.core.services.cloud.CloudComputeService.InstanceStatus;
import org.auscope.portal.core.test.PortalTestClass;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.InstanceState;

public class TestCloudComputeServiceAws extends PortalTestClass{

    private class TestableJob extends CloudJob {

    }

    private class TestableCCS extends CloudComputeServiceAws {

        private AmazonEC2Client testableClient;

        public TestableCCS(AmazonEC2Client client) {
            super("", "");
            testableClient = client;
        }

        protected AmazonEC2 getEc2Client(CloudJob job) throws PortalServiceException {
            return testableClient;
        }
    }

    private AmazonEC2Client mockClient = context.mock(AmazonEC2Client.class);
    private DescribeInstanceStatusResult mockDescribeResult = context.mock(DescribeInstanceStatusResult.class);
    private com.amazonaws.services.ec2.model.InstanceStatus mockStatus = context.mock(com.amazonaws.services.ec2.model.InstanceStatus.class);
    private InstanceState mockState = context.mock(InstanceState.class);
    private CloudComputeServiceAws service;

    @Before
    public void setup() {
        service = new TestableCCS(mockClient);
    }

    @Test
    public void testJobStatus_ParseRunning() throws Exception {
        CloudJob job = new TestableJob();

        job.setComputeInstanceId("testable-id");
        job.setProperty(CloudJob.PROPERTY_STS_ARN, "sts-arn");
        job.setProperty(CloudJob.PROPERTY_CLIENT_SECRET, "client-secret");

        context.checking(new Expectations() {{
            oneOf(mockClient).describeInstanceStatus(with(any(DescribeInstanceStatusRequest.class)));
            will(returnValue(mockDescribeResult));

            allowing(mockDescribeResult).getInstanceStatuses();
            will(returnValue(Arrays.asList(mockStatus)));

            allowing(mockStatus).getInstanceState();
            will(returnValue(mockState));

            allowing(mockState).getName();
            will(returnValue("running"));
        }});

        Assert.assertEquals(InstanceStatus.Running, service.getJobStatus(job));
    }

    @Test
    public void testJobStatus_ParsePending() throws Exception {
        CloudJob job = new TestableJob();

        Date now = new Date();
        Date submitTime = new Date(now.getTime() - (CloudComputeServiceAws.STATUS_PENDING_SECONDS * 1000) - 1000);

        job.setComputeInstanceId("testable-id");
        job.setProperty(CloudJob.PROPERTY_STS_ARN, "sts-arn");
        job.setProperty(CloudJob.PROPERTY_CLIENT_SECRET, "client-secret");

        context.checking(new Expectations() {{
            oneOf(mockClient).describeInstanceStatus(with(any(DescribeInstanceStatusRequest.class)));
            will(returnValue(mockDescribeResult));

            allowing(mockDescribeResult).getInstanceStatuses();
            will(returnValue(Arrays.asList(mockStatus)));

            allowing(mockStatus).getInstanceState();
            will(returnValue(mockState));

            allowing(mockState).getName();
            will(returnValue("pending"));
        }});

        Assert.assertEquals(InstanceStatus.Pending, service.getJobStatus(job));
    }

    @Test
    public void testJobStatus_ParseTerminated() throws Exception {
        CloudJob job = new TestableJob();

        job.setComputeInstanceId("testable-id");
        job.setProperty(CloudJob.PROPERTY_STS_ARN, "sts-arn");
        job.setProperty(CloudJob.PROPERTY_CLIENT_SECRET, "client-secret");

        context.checking(new Expectations() {{
            oneOf(mockClient).describeInstanceStatus(with(any(DescribeInstanceStatusRequest.class)));
            will(returnValue(mockDescribeResult));

            allowing(mockDescribeResult).getInstanceStatuses();
            will(returnValue(Arrays.asList(mockStatus)));

            allowing(mockStatus).getInstanceState();
            will(returnValue(mockState));

            allowing(mockState).getName();
            will(returnValue("terminated"));
        }});

        Assert.assertEquals(InstanceStatus.Missing, service.getJobStatus(job));
    }

    @Test
    public void testJobStatus_ParseMissingException() throws Exception {
        CloudJob job = new TestableJob();

        job.setComputeInstanceId("testable-id");
        job.setProperty(CloudJob.PROPERTY_STS_ARN, "sts-arn");
        job.setProperty(CloudJob.PROPERTY_CLIENT_SECRET, "client-secret");

        final AmazonServiceException ex = new AmazonServiceException("Testing Exception");
        ex.setErrorCode("InvalidInstanceID.NotFound");

        context.checking(new Expectations() {{
            oneOf(mockClient).describeInstanceStatus(with(any(DescribeInstanceStatusRequest.class)));
            will(throwException(ex));
        }});

        Assert.assertEquals(InstanceStatus.Missing, service.getJobStatus(job));
    }

    @Test
    public void testJobStatus_NewJobPending() throws Exception {
        CloudJob job = new TestableJob();

        Date now = new Date();
        Date submitTime = new Date(now.getTime() - (CloudComputeServiceAws.STATUS_PENDING_SECONDS * 1000) + 1000);

        job.setComputeInstanceId("testable-id");
        job.setProperty(CloudJob.PROPERTY_STS_ARN, "sts-arn");
        job.setProperty(CloudJob.PROPERTY_CLIENT_SECRET, "client-secret");
        job.setSubmitDate(submitTime);

        final AmazonServiceException ex = new AmazonServiceException("Testing Exception");
        ex.setErrorCode("InvalidInstanceID.NotFound");

        context.checking(new Expectations() {{

        }});

        Assert.assertEquals(InstanceStatus.Pending, service.getJobStatus(job));
    }

    /**
     * In case we have some time issues and a job's submit date is in the future
     *
     * In this case we expect it to return pending. If it's only a few seconds in the future then it's probably just a minor date/time
     * shifting error (or a daylight savings time shift). If it's a LONG time in the future, what can we expect? It's probably overengineering
     * the checks if we start accounting for the latter.
     *
     * @throws Exception
     */
    @Test
    public void testJobStatus_FutureJob() throws Exception {
        CloudJob job = new TestableJob();

        Date now = new Date();
        Date submitTime = new Date(now.getTime() + TimeUnit.DAYS.toMillis(2)); //Throw the submit 2 days into the future to simulate some weird clock behavior

        job.setComputeInstanceId("testable-id");
        job.setProperty(CloudJob.PROPERTY_STS_ARN, "sts-arn");
        job.setProperty(CloudJob.PROPERTY_CLIENT_SECRET, "client-secret");
        job.setSubmitDate(submitTime);

        context.checking(new Expectations() {{

        }});

        Assert.assertEquals(InstanceStatus.Pending, service.getJobStatus(job));
    }

    @Test(expected=PortalServiceException.class)
    public void testStsRequired() throws Exception {
        final TestableCCS stsService = new TestableCCS(mockClient);
        stsService.setRequireSts(true);
        stsService.getCredentials(null, null);
    }

    @Test(expected=PortalServiceException.class)
    public void testJobStatus_BadResponse() throws Exception {
        CloudJob job = new TestableJob();

        job.setComputeInstanceId("testable-id");
        job.setProperty(CloudJob.PROPERTY_STS_ARN, "sts-arn");
        job.setProperty(CloudJob.PROPERTY_CLIENT_SECRET, "client-secret");

        final AmazonServiceException ex = new AmazonServiceException("Testing Exception");
        ex.setErrorCode("unrecognized-ID");
        ex.setStatusCode(503);

        context.checking(new Expectations() {{
            oneOf(mockClient).describeInstanceStatus(with(any(DescribeInstanceStatusRequest.class)));
            will(throwException(ex));
        }});

        service.getJobStatus(job);
    }
}