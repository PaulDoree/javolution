//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.08.17 at 09:13:55 PM EDT 
//


package org.javolution.xml.jaxb.common.test.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for testUnsignedElement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="testUnsignedElement">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="testUnsignedIntElement" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="testUnsignedShortElement" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" minOccurs="0"/>
 *         &lt;element name="testUnsignedByteElement" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "testUnsignedElement", propOrder = {
    "testUnsignedIntElement",
    "testUnsignedShortElement",
    "testUnsignedByteElement"
})
public class TestUnsignedElement {

    @XmlSchemaType(name = "unsignedInt")
    protected Long testUnsignedIntElement;
    @XmlSchemaType(name = "unsignedShort")
    protected Integer testUnsignedShortElement;
    @XmlSchemaType(name = "unsignedByte")
    protected Short testUnsignedByteElement;

    /**
     * Gets the value of the testUnsignedIntElement property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTestUnsignedIntElement() {
        return testUnsignedIntElement;
    }

    /**
     * Sets the value of the testUnsignedIntElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTestUnsignedIntElement(Long value) {
        this.testUnsignedIntElement = value;
    }

    /**
     * Gets the value of the testUnsignedShortElement property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTestUnsignedShortElement() {
        return testUnsignedShortElement;
    }

    /**
     * Sets the value of the testUnsignedShortElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTestUnsignedShortElement(Integer value) {
        this.testUnsignedShortElement = value;
    }

    /**
     * Gets the value of the testUnsignedByteElement property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getTestUnsignedByteElement() {
        return testUnsignedByteElement;
    }

    /**
     * Sets the value of the testUnsignedByteElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setTestUnsignedByteElement(Short value) {
        this.testUnsignedByteElement = value;
    }

}