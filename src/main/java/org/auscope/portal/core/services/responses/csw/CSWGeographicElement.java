package org.auscope.portal.core.services.responses.csw;

import org.springframework.data.elasticsearch.core.geo.GeoJsonPolygon;

/**
 * An interface representing abstract geometry that can bound a record in a CSW response.
 */
public interface CSWGeographicElement {
    
    /**
     * Gets the west bound longitude.
     *
     * @return the west bound longitude
     */
    public double getWestBoundLongitude();

    /**
     * Sets the west bound longitude.
     *
     * @param westBoundLongitude
     *            the new west bound longitude
     */
    public void setWestBoundLongitude(double westBoundLongitude);

    /**
     * Gets the east bound longitude.
     *
     * @return the east bound longitude
     */
    public double getEastBoundLongitude();

    /**
     * Sets the east bound longitude.
     *
     * @param eastBoundLongitude
     *            the new east bound longitude
     */
    public void setEastBoundLongitude(double eastBoundLongitude);

    /**
     * Gets the south bound latitude.
     *
     * @return the south bound latitude
     */
    public double getSouthBoundLatitude();

    /**
     * Sets the south bound latitude.
     *
     * @param southBoundLatitude
     *            the new south bound latitude
     */
    public void setSouthBoundLatitude(double southBoundLatitude);

    /**
     * Gets the north bound latitude.
     *
     * @return the north bound latitude
     */
    public double getNorthBoundLatitude();

    /**
     * Sets the north bound latitude.
     *
     * @param northBoundLatitude
     *            the new north bound latitude
     */
    public void setNorthBoundLatitude(double northBoundLatitude);
    
    /**
     * Get the GeoJsonPolygon instance of the bounding box
     * 
     * @return the GeoJsonPolygon instance of the bounding box
     */
    public GeoJsonPolygon getBoundingPolygon();
    
    /**
     * Set the GeoJsonPolygon instance of the bounding box
     * 
     * @param boundingPolygon
     */
    public void setBoundingPolygon(GeoJsonPolygon boundingPolygon);
    
    /**
     * Sets the bounding GeoJsonPolygon from lat/lon points
     * 
     * @param westBoundLongitude the west bound longitude
     * @param eastBoundLongitude the east bound longitude
     * @param southBoundLatitude the south bound latitude
     * @param northBoundLatitude the north bound latitude
     */
    public void setBoundingPolygon(double westBoundLongitude, double eastBoundLongitude, double southBoundLatitude, double northBoundLatitude);
    
    /**
     * Gets the missing source coordinates field
     * 
     * @return true iff this was constructed with missing source coords and a global default had to be substituted
     */
    public boolean hasMissingCoords();
}
