
package remote.wise.EAintegration.clientPackage.AssetSaleFromD2C;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.AssetSaleFromD2C package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AssetSaleFromDealerToCust_QNAME = new QName("http://webservice.service.wise.remote/", "assetSaleFromDealerToCust");
    private final static QName _AssetSaleFromDealerToCustResponse_QNAME = new QName("http://webservice.service.wise.remote/", "assetSaleFromDealerToCustResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.AssetSaleFromD2C
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AssetSaleFromDealerToCustResponse }
     * 
     */
    public AssetSaleFromDealerToCustResponse createAssetSaleFromDealerToCustResponse() {
        return new AssetSaleFromDealerToCustResponse();
    }

    /**
     * Create an instance of {@link AssetSaleFromDealerToCust }
     * 
     */
    public AssetSaleFromDealerToCust createAssetSaleFromDealerToCust() {
        return new AssetSaleFromDealerToCust();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AssetSaleFromDealerToCust }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "assetSaleFromDealerToCust")
    public JAXBElement<AssetSaleFromDealerToCust> createAssetSaleFromDealerToCust(AssetSaleFromDealerToCust value) {
        return new JAXBElement<AssetSaleFromDealerToCust>(_AssetSaleFromDealerToCust_QNAME, AssetSaleFromDealerToCust.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AssetSaleFromDealerToCustResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "assetSaleFromDealerToCustResponse")
    public JAXBElement<AssetSaleFromDealerToCustResponse> createAssetSaleFromDealerToCustResponse(AssetSaleFromDealerToCustResponse value) {
        return new JAXBElement<AssetSaleFromDealerToCustResponse>(_AssetSaleFromDealerToCustResponse_QNAME, AssetSaleFromDealerToCustResponse.class, null, value);
    }

}
