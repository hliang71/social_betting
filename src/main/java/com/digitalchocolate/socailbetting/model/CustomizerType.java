//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.11 at 02:24:23 PM PST 
//


package com.digitalchocolate.socailbetting.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;


/**
 * <p>Java class for customizerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="customizerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="content" type="{}contentType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name="customizer")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "customizerType", propOrder = {
    "content"
})
public class CustomizerType {

    @XmlElement(required = true)
    protected List<ContentType> content;

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link ContentType }
     *     
     */
    public List<ContentType> getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentType }
     *     
     */
    public void setContent(List<ContentType> value) {
        this.content = value;
    }
    @Override
    public String toString()
    {
    	StringBuilder builder = new StringBuilder("customizer : \n");
    	for(ContentType con : content)
    	{
	    	builder.append("content:\n");
	    	if(this.content != null)
	    	{
	    		builder.append(con.toString());
	    	}
    	}
    	return builder.toString();
    }
}