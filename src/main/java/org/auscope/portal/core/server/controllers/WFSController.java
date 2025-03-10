package org.auscope.portal.core.server.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.auscope.portal.core.services.WFSService;
import org.auscope.portal.core.services.methodmakers.filter.FilterBoundingBox;
import org.auscope.portal.core.services.methodmakers.filter.SimpleBBoxFilter;
import org.auscope.portal.core.services.responses.wfs.WFSResponse;
import org.auscope.portal.core.services.responses.wfs.WFSTransformedResponse;
import org.auscope.portal.core.util.FileIOUtil;
import org.auscope.portal.core.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Acts as a proxy to WFS's
 *
 * User: Mathew Wyatt
 *
 * @version $Id$
 */

@Controller
public class WFSController extends BasePortalController {

    private WFSService wfsService;

    @Autowired
    public WFSController(WFSService wfsService) {
        this.wfsService = wfsService;
    }
    
    /**
     * Given a service Url and a feature type this will query for all of the features, then convert them into KML, to be displayed, assuming that the response
     * will be complex feature GeoSciML
     *
     * @param serviceUrl
     * @param featureType
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/getAllFeatures.do")
    public ModelAndView requestAllFeatures(@RequestParam("serviceUrl") final String serviceUrl,
            @RequestParam("typeName") final String featureType,
            @RequestParam(required = false, value = "bbox") final String bboxJSONString,
            @RequestParam(required = false, value = "maxFeatures", defaultValue = "0") int maxFeatures)
            throws Exception {

        FilterBoundingBox bbox = FilterBoundingBox.attemptParseFromJSON(bboxJSONString);

        SimpleBBoxFilter filter = new SimpleBBoxFilter();
        String filterString = null;
        String srs = null;
        if (bbox == null) {
            filterString = filter.getFilterStringAllRecords();
        } else {
            filterString = filter.getFilterStringBoundingBox(bbox);
        }

        WFSResponse response = null;
        try {
            response = wfsService.getWfsResponse(serviceUrl, featureType, filterString, maxFeatures, srs);
        } catch (Exception ex) {
            log.warn(String.format("Exception getting '%2$s' from '%1$s': %3$s", serviceUrl, featureType, ex));
            log.debug("Exception: ", ex);
            return generateExceptionResponse(ex, serviceUrl);
        }

        return generateNamedJSONResponseMAV(true, "gml", response.getData(), response.getMethod());
    }
    
    
    /**
     * Given a service Url and a feature type this will query for all of the features, then convert them into KML, to be displayed, assuming that the response
     * will be complex feature GeoSciML
     *
     * @param serviceUrl
     * @param featureType
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/getAllFeaturesInCSV.do")
    public void getAllFeaturesInCSV(@RequestParam("serviceUrl") final String serviceUrl,
            @RequestParam("typeName") final String featureType,
            @RequestParam(required = false, value = "bbox") final String bboxJSONString,
            @RequestParam(required=false, value="filter") String filter,
            @RequestParam(required=false, value="maxFeatures",defaultValue = "100000") Integer maxFeatures,
            HttpServletResponse response)
            throws Exception {
    	// Some WFS URL had type/subtypes with spaces, need to URI encode
    	String url = HttpUtil.encodeURL(serviceUrl);
        FilterBoundingBox bbox = FilterBoundingBox.attemptParseFromJSON(bboxJSONString);
        response.setContentType("text/csv");
        OutputStream outputStream = response.getOutputStream();
        SimpleBBoxFilter bboxFilter = new SimpleBBoxFilter();
        String filterString = null;
        InputStream result = null;
        try {
            if (filter != null && filter.indexOf("ogc:Filter")>0) { //Polygon filter
                filterString = filter.replace("gsmlp:shape","erl:shape");
                result = wfsService.downloadCSVByPolygonFilter(url.toString(), featureType, filterString, maxFeatures);
            } else{ //BBox or no filter
                if (bbox == null) {
                    filterString = bboxFilter.getFilterStringAllRecords();
                    result = wfsService.downloadCSV(url.toString(), featureType, filterString, maxFeatures);
                } else {
                    result = wfsService.downloadCSVByBBox(url.toString(), featureType, bbox.toBBoxString(), maxFeatures);
                }
            }
        } catch (Exception ex) {
            log.warn(String.format("Exception getting '%2$s' from '%1$s': %3$s", url.toString(), featureType, ex));
            log.debug("Exception: ", ex);           
        }
        
        

        FileIOUtil.writeInputToOutputStream(result, outputStream, 8 * 1024, true);
        outputStream.close();
    }

    /**
     * Given a service Url, a feature type and a specific feature ID, this function will fetch the specific feature and then convert it into KML to be
     * displayed, assuming that the response will be complex feature GeoSciML
     *
     * @param serviceUrl
     * @param featureType
     * @param featureId
     * @param request
     * @return
     */
    @RequestMapping("/requestFeature.do")
    public ModelAndView requestFeature(@RequestParam("serviceUrl") final String serviceUrl,
            @RequestParam("typeName") final String featureType,
            @RequestParam("featureId") final String featureId) throws Exception {
        WFSResponse response = null;
        try {
            response = wfsService.getWfsResponse(serviceUrl, featureType, featureId);
        } catch (Exception ex) {
            log.warn(String.format("Exception getting '%2$s' with id '%4$s' from '%1$s': %3$s", serviceUrl,
                    featureType, ex, featureId));
            log.debug("Exception: ", ex);
            return generateExceptionResponse(ex, serviceUrl);
        }

        return generateNamedJSONResponseMAV(true, "gml", response.getData(), response.getMethod());
    }

    /**
     * Encodes the url, so that any space characters are encoded as %20
     * note: tried various encoding libraries but they encode other characters in the url, i.e. /
     * 
     * @param value
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String encodeValue(String value) throws UnsupportedEncodingException {
        String rawPath = value.replace(" " ,  "%20");
        return rawPath;
    }
    
    /**
     * This method can be utilised by specifying a WFS url, typeName and featureId (in which a WFS request will be generated) OR just by specifying a URL which
     * will be resolved (such as in the case of a resolvable URN which maps to a WFS request at a remote server).
     *
     * @param serviceUrl
     *            Can either be a WFS endpoint OR a URL that when resolved returns a WFS response
     * @param typeName
     *            [Optional] If specified a WFS request will be generated
     * @param featureId
     *            [Optional] If specified a WFS request will be generated
     * @throws IOException
     */
    @RequestMapping("wfsFeaturePopup.do")
    public void wfsFeaturePopup(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("url") String serviceUrl,
            @RequestParam(required = false, value = "typeName") String typeName,
            @RequestParam(required = false, value = "featureId") String featureId) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        ServletOutputStream outputStream = response.getOutputStream();

        // Create request base URL
        StringBuffer requestUrl = request.getRequestURL();
        int startPos = requestUrl.indexOf("/wfsFeaturePopup.do");
        requestUrl.setLength(startPos);
        serviceUrl = encodeValue(serviceUrl);
        
        // If a system portalUrl property has been set, use that instead of the
        // request URL which may be missing path fragments
        String portalUrl = requestUrl.toString();
        if (System.getProperty("portalUrl") != null) {
        	portalUrl = System.getProperty("portalUrl");
        }
        
        //Make our request, transform and then return it.
        WFSTransformedResponse htmlResponse = null;
        try {
            if (typeName == null) {
                htmlResponse = wfsService.getWfsResponseAsHtml(serviceUrl, portalUrl);
            } else {
                htmlResponse = wfsService.getWfsResponseAsHtml(serviceUrl, typeName, featureId, portalUrl);
            }

            outputStream.write(htmlResponse.getTransformed().getBytes());
        } catch (Exception ex) {
            log.warn(String.format("Internal error requesting/writing popup for '%1$s' from '%2$s': %3$s", typeName,
                    serviceUrl, ex));
            log.debug("Exception: ", ex);
            // Create a wrapper for the servlet response as the output stream has already been consumed
            new HttpServletResponseWrapper(response).sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This method converts supplied WFS string from WMS pop up into HTML.
     * @param gml    a WFS feature in GML format
     * @throws Exception
     */
    @RequestMapping(value="transformToHtmlPopup.do", method = {RequestMethod.GET, RequestMethod.POST})
    public void transformToHtml(HttpServletRequest request, HttpServletResponse response, @RequestParam("gml") String gml) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        ServletOutputStream outputStream = response.getOutputStream();
        
        // Create request base URL
        StringBuffer requestURL = request.getRequestURL();
        int startPos = requestURL.lastIndexOf("/transformToHtmlPopup.do");
        requestURL.setLength(startPos);
        
        // If a system portalUrl property has been set, use that instead of the
        // request URL which may be missing path fragments
        String portalUrl= requestURL.toString();
        if (System.getProperty("portalUrl") != null) {
        	portalUrl = System.getProperty("portalUrl");
        }
        
        //Make our request, transform and then return it.
        WFSTransformedResponse htmlResponse = null;
        WFSService service = wfsService;
        try {
            htmlResponse = service.transformToHtml(gml, null, portalUrl);
            outputStream.write(htmlResponse.getTransformed().getBytes());
        } catch (Exception ex) {
            log.warn(String.format("Internal error requesting/writing popup for '%1$s': %3$s", gml, ex));
            log.debug("Exception: ", ex);
            // Create a wrapper for the servlet response as the output stream has already been consumed
            new HttpServletResponseWrapper(response).sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
