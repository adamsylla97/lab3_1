import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Matches;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.invoicing.*;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookKeeperTest {

    @Test
    public void invoiceRequestWithOnePositionShouldReturnInvoiceWithOnePositionTest(){

        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        ClientData clientData = new ClientData(Id.generate(),"client");
        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.STANDARD, new Money(3))).thenReturn(new Tax(new Money(0.23),"23%"));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        RequestItem requestItem = new RequestItem(productData, 5, new Money(3));
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest,taxPolicy);

        Assert.assertThat(invoice.getItems().size(),is(equalTo(1)));

    }

}
