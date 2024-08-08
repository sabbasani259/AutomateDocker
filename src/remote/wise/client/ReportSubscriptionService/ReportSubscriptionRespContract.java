
package remote.wise.client.ReportSubscriptionService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reportSubscriptionRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reportSubscriptionRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contactId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="monthlyReportSubscription" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="reportId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="reportName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weeklyReportSubscription" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reportSubscriptionRespContract", propOrder = {
    "contactId",
    "monthlyReportSubscription",
    "reportId",
    "reportName",
    "weeklyReportSubscription"
})
public class ReportSubscriptionRespContract {

    protected String contactId;
    protected boolean monthlyReportSubscription;
    protected int reportId;
    protected String reportName;
    protected boolean weeklyReportSubscription;

    /**
     * Gets the value of the contactId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactId() {
        return contactId;
    }

    /**
     * Sets the value of the contactId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactId(String value) {
        this.contactId = value;
    }

    /**
     * Gets the value of the monthlyReportSubscription property.
     * 
     */
    public boolean isMonthlyReportSubscription() {
        return monthlyReportSubscription;
    }

    /**
     * Sets the value of the monthlyReportSubscription property.
     * 
     */
    public void setMonthlyReportSubscription(boolean value) {
        this.monthlyReportSubscription = value;
    }

    /**
     * Gets the value of the reportId property.
     * 
     */
    public int getReportId() {
        return reportId;
    }

    /**
     * Sets the value of the reportId property.
     * 
     */
    public void setReportId(int value) {
        this.reportId = value;
    }

    /**
     * Gets the value of the reportName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportName() {
        return reportName;
    }

    /**
     * Sets the value of the reportName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportName(String value) {
        this.reportName = value;
    }

    /**
     * Gets the value of the weeklyReportSubscription property.
     * 
     */
    public boolean isWeeklyReportSubscription() {
        return weeklyReportSubscription;
    }

    /**
     * Sets the value of the weeklyReportSubscription property.
     * 
     */
    public void setWeeklyReportSubscription(boolean value) {
        this.weeklyReportSubscription = value;
    }

}
