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
import static org.mockito.Mockito.*;

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

    @Test
    public void invoiceRequestWithNoPositionsShouldReturnInvoiceWithZeroPositionsTest() {

        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        ClientData clientData = new ClientData(Id.generate(),"client");
        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.STANDARD, new Money(3))).thenReturn(new Tax(new Money(0.23),"23%"));

        Invoice invoice = bookKeeper.issuance(invoiceRequest,taxPolicy);

        Assert.assertThat(invoice.getItems().size(),is(equalTo(0)));

    }


    @Test
    public void invoiceRequestWithTwoPositionsShouldCallCalculateTaxTwoTimesTest(){

        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        ClientData clientData = new ClientData(Id.generate(),"client");
        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);

        Money money1 = new Money(3);
        Money money2 = new Money(5);

        TaxPolicy taxPolicy1 = mock(TaxPolicy.class);
        when(taxPolicy1.calculateTax(ProductType.STANDARD, money1)).thenReturn(new Tax(new Money(0.23),"23%"));
        when(taxPolicy1.calculateTax(ProductType.FOOD,money2)).thenReturn(new Tax(new Money(0.46),"46%"));

        ProductData productData1 = mock(ProductData.class);
        when(productData1.getType()).thenReturn(ProductType.STANDARD);

        ProductData productData2 = mock(ProductData.class);
        when(productData2.getType()).thenReturn(ProductType.FOOD);

        RequestItem requestItem1 = new RequestItem(productData1, 5, new Money(3));
        invoiceRequest.add(requestItem1);

        RequestItem requestItem2 = new RequestItem(productData2, 2, new Money(5));
        invoiceRequest.add(requestItem2);

        Invoice invoice = bookKeeper.issuance(invoiceRequest,taxPolicy1);

        Mockito.verify(taxPolicy1, times(1)).calculateTax(ProductType.STANDARD, money1);
        Mockito.verify(taxPolicy1, times(1)).calculateTax(ProductType.FOOD, money2);

    }

}
