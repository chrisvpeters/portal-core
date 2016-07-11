package org.auscope.portal.core.services.responses.csw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.auscope.portal.core.services.csw.CSWRecordsFilterVisitor;

/**
 * Represents a parsed gmd:MD_Metadata element that is received as part of an OGC CSW transaction.
 *
 * @author Mathew Wyatt
 * @author Joshua Vote
 * @version $Id$
 */
public class CSWRecord {

    /** The Constant logger. */
    private static final Log logger = LogFactory.getLog(CSWRecord.class);

    /** The service name. */
    private String serviceName;

    /** The online resources. */
    private AbstractCSWOnlineResource[] onlineResources;

    /** The resource provider. */
    private String resourceProvider;

    /** The file identifier. */
    private String fileIdentifier;

    /** The parent identifier. */
    private String parentIdentifier;

    /** The record info url. */
    private String recordInfoUrl;

    /** The csw geographic elements. */
    private CSWGeographicElement[] cswGeographicElements;

    /** The descriptive keywords. */
    private String[] descriptiveKeywords;

    /** The URIs from which file downloads will be available in some records. */
    private String[] dataSetURIs;

    /** The data identification abstract. */
    private String dataIdentificationAbstract;

    /** The supplemental information. */
    private String supplementalInformation;

    /** The language. */
    private String language;

    /** The constraints. */
    private String[] constraints;

    /** The contact. */
    private CSWResponsibleParty contact;

    /** The date. */
    private Date date;

    /** The data quality statement. */
    private String dataQualityStatement;

    /** The csw child records */
    private List<CSWRecord> childRecords = new ArrayList<>();

    private boolean noCache;

    private boolean service;

    private String layerName = "";

    /**
     * Instantiates a new empty CSWRecord
     *
     * @param fileIdentifier
     */
    public CSWRecord(final String fileIdentifier) {
        this(null, fileIdentifier, null, null, null, null, "");
    }

    /**
     * Instantiates a new CSW record.
     *
     * @param serviceName
     *            the service name
     * @param fileIdentifier
     *            the file identifier
     * @param recordInfoUrl
     *            the record info url
     * @param dataIdentificationAbstract
     *            the data identification abstract
     * @param onlineResources
     *            the online resources
     * @param cswGeographicsElements
     *            the csw geographics elements
     */
    public CSWRecord(final String serviceName, final String fileIdentifier,
            final String recordInfoUrl, final String dataIdentificationAbstract,
            final AbstractCSWOnlineResource[] onlineResources, final CSWGeographicElement[] cswGeographicsElements) {
        this(serviceName, fileIdentifier, recordInfoUrl, dataIdentificationAbstract, onlineResources, cswGeographicsElements, "");
    }

    /**
     * Instantiates a new CSW record.
     *
     * @param serviceName
     *            the service name
     * @param fileIdentifier
     *            the file identifier
     * @param recordInfoUrl
     *            the record info url
     * @param dataIdentificationAbstract
     *            the data identification abstract
     * @param onlineResources
     *            the online resources
     * @param cswGeographicsElements
     *            the csw geographics elements
     */
    public CSWRecord(final String serviceName, final String fileIdentifier,
            final String recordInfoUrl, final String dataIdentificationAbstract,
            final AbstractCSWOnlineResource[] onlineResources, final CSWGeographicElement[] cswGeographicsElements, final String layerName) {
        this.serviceName = serviceName;
        this.fileIdentifier = fileIdentifier;
        this.recordInfoUrl = recordInfoUrl;
        this.dataIdentificationAbstract = dataIdentificationAbstract;
        this.onlineResources = onlineResources;
        this.cswGeographicElements = cswGeographicsElements;
        this.supplementalInformation = "";
        this.descriptiveKeywords = new String[0];
        this.dataSetURIs = new String[0];
        this.constraints = new String[0];
        this.noCache = false;
        this.layerName = layerName;
        logger.trace(this.toString());
    }

    /**
     * Sets the record info url.
     *
     * @param recordInfoUrl
     *            the new record info url
     */
    public void setRecordInfoUrl(final String recordInfoUrl) {
        this.recordInfoUrl = recordInfoUrl;
    }

    /**
     * Gets the record info url.
     *
     * @return the record info url
     */
    public String getRecordInfoUrl() {
        return recordInfoUrl;
    }

    /**
     * Sets the noCache variable
     *
     * @param recordInfoUrl
     *            the new record info url
     */
    public void setNoCache(final boolean nocache) {
        this.noCache = nocache;
    }

    /**
     * Gets the noCache variable
     *
     * @return the record info url
     */
    public boolean getNoCache() {
        return this.noCache;
    }

    /**
     * Gets the file identifier.
     *
     * @return the file identifier
     */
    public String getFileIdentifier() {
        return fileIdentifier;
    }

    /**
     * Get the parent identifier.
     *
     */
    public String getParentIdentifier() {
        return parentIdentifier;
    }

    /**
     * Gets the service name.
     *
     * @return the service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Gets the online resources.
     *
     * @return the online resources
     */
    public AbstractCSWOnlineResource[] getOnlineResources() {
        return onlineResources;
    }

    /**
     * Gets the resource provider.
     *
     * @return the resource provider
     */
    public String getResourceProvider() {
        return resourceProvider;
    }

    /**
     * Gets the data identification abstract.
     *
     * @return the data identification abstract
     */
    public String getDataIdentificationAbstract() {
        return dataIdentificationAbstract;
    }

    /**
     * Set the CSWGeographicElement that bounds this record.
     *
     * @param cswGeographicElements
     *            the new cSW geographic elements
     */
    public void setCSWGeographicElements(final CSWGeographicElement[] cswGeographicElements) {
        this.cswGeographicElements = cswGeographicElements;
    }

    /**
     * gets the CSWGeographicElement that bounds this record (or null if it DNE).
     *
     * @return the cSW geographic elements
     */
    public CSWGeographicElement[] getCSWGeographicElements() {
        return cswGeographicElements;
    }

    /**
     * Returns the descriptive keywords for this record.
     *
     * @return descriptive keywords
     */
    public String[] getDescriptiveKeywords() {
        return descriptiveKeywords;
    }

    /**
     * Returns the dataset URIs for this record.
     *
     * @return array of URIs
     */
    public String[] getDataSetURIs() {
        return dataSetURIs;
    }

    /**
     * Gets the constraints.
     *
     * @return the constraints
     */
    public String[] getConstraints() {
        return constraints;
    }

    /**
     * Sets the constraints.
     *
     * @param constraints
     *            the new constraints
     */
    public void setConstraints(final String[] constraints) {
        this.constraints = constraints;
    }

    /**
     * Sets the service name.
     *
     * @param serviceName
     *            the new service name
     */
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Sets the online resources.
     *
     * @param onlineResources
     *            the new online resources
     */
    public void setOnlineResources(final AbstractCSWOnlineResource[] onlineResources) {
        this.onlineResources = onlineResources;
    }

    /**
     * Sets the resource provider.
     *
     * @param resourceProvider
     *            the new resource provider
     */
    public void setResourceProvider(final String resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    /**
     * Sets the file identifier.
     *
     * @param fileIdentifier
     *            the new file identifier
     */
    public void setFileIdentifier(final String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
    }

    /**
     * Sets the parent identifier.
     *
     * @param parentFileIdentifier
     *            the new parent file identifier
     */
    public void setParentIdentifier(final String parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

    /**
     * Sets the descriptive keywords.
     *
     * @param descriptiveKeywords
     *            the new descriptive keywords
     */
    public void setDescriptiveKeywords(final String[] descriptiveKeywords) {
        this.descriptiveKeywords = descriptiveKeywords;
    }

    /**
     * Sets the dataset URIs
     *
     * @param dataSetURIs
     *            the dataSetURIs to set
     */
    public void setDataSetURIs(final String[] dataSetURIs) {
        this.dataSetURIs = dataSetURIs;
    }

    /**
     * Sets the data identification abstract.
     *
     * @param dataIdentificationAbstract
     *            the new data identification abstract
     */
    public void setDataIdentificationAbstract(final String dataIdentificationAbstract) {
        this.dataIdentificationAbstract = dataIdentificationAbstract;
    }

    /**
     * Gets the supplemental information.
     *
     * @return the supplemental information
     */
    public String getSupplementalInformation() {
        return supplementalInformation;
    }

    /**
     * Sets the supplemental information.
     *
     * @param supplementalInformation
     *            the new supplemental information
     */
    public void setSupplementalInformation(final String supplementalInformation) {
        this.supplementalInformation = supplementalInformation;
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language
     *            the new language
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * Gets the contact.
     *
     * @return the contact
     */
    public CSWResponsibleParty getContact() {
        return contact;
    }

    /**
     * Sets the contact.
     *
     * @param contact
     *            the new contact
     */
    public void setContact(final CSWResponsibleParty contact) {
        this.contact = contact;
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date
     *            the new date
     */
    public void setDate(final Date date) {
        this.date = date;
    }

    /**
     * Gets the data quality statement.
     *
     * @return the data quality statement
     */
    public String getDataQualityStatement() {
        return dataQualityStatement;
    }

    /**
     * Sets the data quality statement.
     *
     * @param dataQualityStatement
     *            the new data quality statement
     */
    public void setDataQualityStatement(final String dataQualityStatement) {
        this.dataQualityStatement = dataQualityStatement;
    }

    /**
     * Sets the child records of this record.
     *
     * @param childRecords
     *            an array of child records for this csw record
     */
    public void addChildRecord(final CSWRecord childRecord) {
        this.childRecords.add(childRecord);
    }

    /**
     * Gets the child records.
     *
     * @return the csw child records
     */
    public CSWRecord[] getChildRecords() {
        return childRecords.toArray(new CSWRecord[childRecords.size()]);
    }

    /**
     * Checks to see if this record has any child metadata records.
     *
     * @return true if this record has any child metadata records
     */
    public boolean hasChildRecords() {
        return childRecords.size() > 0;
    }

    /**
     * @return the layerName
     */
    public String getLayerName() {
        return layerName;
    }

    /**
     * @param layerName the layerName to set
     */
    public void setLayerName(final String layerName) {
        this.layerName = layerName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CSWRecord [serviceName=" + serviceName + ", onlineResources="
                + Arrays.toString(onlineResources) + ", resourceProvider="
                + resourceProvider + ", fileIdentifier=" + fileIdentifier
                + ", recordInfoUrl=" + recordInfoUrl
                + ", cswGeographicElements="
                + Arrays.toString(cswGeographicElements)
                + ", descriptiveKeywords="
                + Arrays.toString(descriptiveKeywords)
                + ", datasetURIs="
                + Arrays.toString(dataSetURIs)
                + ", dataIdentificationAbstract=" + dataIdentificationAbstract
                + ", supplementalInformation=" + supplementalInformation
                + ", language=" + language + ", constraints="
                + Arrays.toString(constraints) + ", contact=" + contact
                + ", date=" + date + ", childRecords="
                + childRecords + ", layerName=" + layerName + "]";
    }

    /**
     * Returns a filtered list of online resource protocols that match at least one of the specified types.
     *
     * @param types
     *            The list of types you want to filter by
     * @return the online resources by type
     */
    public AbstractCSWOnlineResource[] getOnlineResourcesByType(final AbstractCSWOnlineResource.OnlineResourceType... types) {
        final List<AbstractCSWOnlineResource> result = new ArrayList<>();

        for (final AbstractCSWOnlineResource r : onlineResources) {
            if (r == null) {
                continue;
            }
            boolean matching = false;
            final AbstractCSWOnlineResource.OnlineResourceType typeToMatch = r.getType();
            for (final AbstractCSWOnlineResource.OnlineResourceType type : types) {
                if (typeToMatch == type) {
                    matching = true;
                    break;
                }
            }

            if (matching) {
                result.add(r);
            }
        }

        return result.toArray(new AbstractCSWOnlineResource[result.size()]);
    }

    /**
     * Returns a filtered list of online resource protocols that match at least one of the specified types and is accepted by the visitor. Using a
     * CSWRecordsFilterVisitor will open up alot of other filter opportunity in the future without clutering up the code. eg if we need to filter by the
     * onlineResource description, we will just need a visitor that implements CSWRecordsFilterVisitor and apply the appropriate logic.
     *
     * @param types
     *            The list of types you want to filter by
     * @param visitor
     *            visitor to action on the AbstractCSWOnlineResource
     * @return the online resources by type
     */
    public AbstractCSWOnlineResource[] getOnlineResourcesByType(
            final CSWRecordsFilterVisitor visitor,
            final AbstractCSWOnlineResource.OnlineResourceType... types) {
        final List<AbstractCSWOnlineResource> result = new ArrayList<>();

        for (final AbstractCSWOnlineResource r : onlineResources) {
            if (r == null) {
                continue;
            }
            boolean matching = false;
            final AbstractCSWOnlineResource.OnlineResourceType typeToMatch = r
                    .getType();
            for (final AbstractCSWOnlineResource.OnlineResourceType type : types) {
                if (typeToMatch == type) {
                    matching = true;
                    break;
                }
            }

            if (matching && r.accept(visitor)) {
                result.add(r);
            }
        }

        return result.toArray(new AbstractCSWOnlineResource[result.size()]);
    }

    /**
     * Returns true if this CSW Record contains at least 1 onlineResource with ANY of the specified types.
     *
     * @param types
     *            the types
     * @return true, if successful
     */
    public boolean containsAnyOnlineResource(final AbstractCSWOnlineResource.OnlineResourceType... types) {

        if (onlineResources == null) {
            return false;
        }

        for (final AbstractCSWOnlineResource r : onlineResources) {
            if (r != null) {
                final AbstractCSWOnlineResource.OnlineResourceType typeToMatch = r.getType();
                for (final AbstractCSWOnlineResource.OnlineResourceType type : types) {
                    if (typeToMatch == type) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns true if this record contains the given descriptive keyword, false otherwise.
     *
     * @param str
     *            the str
     * @return true if this record contains the given descriptive keyword, false otherwise.
     */
    public boolean containsKeyword(final String str) {
        return Arrays.asList(descriptiveKeywords).contains(str);
    }

    /**
     * Tests equality of a CSWRecord based on file identifier
     */
    @Override
    public boolean equals(final Object o) {
        if (o instanceof CSWRecord) {
            return this.getFileIdentifier().equals(((CSWRecord) o).getFileIdentifier());
        } else {
            return super.equals(o);
        }
    }

    public boolean isService() {
        return service;
    }

    public void setService(final boolean service) {
        this.service = service;
    }

    /**
     * Creates a hashcode based on this record's file identifier
     */
    @Override
    public int hashCode() {
        return this.fileIdentifier.hashCode();
    }
}
