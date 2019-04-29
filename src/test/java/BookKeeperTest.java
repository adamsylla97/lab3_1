import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Matches;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.invoicing.*;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

public class BookKeeperTest {

    BookKeeper bookKeeper;
    ClientData clientData;
    InvoiceRequest invoiceRequest;
    TaxPolicy taxPolicy;
    ProductData productData;
    ProductData productData2;
    Money money1;
    Money money2;

    @Before
    public void setup() {
        bookKeeper = new BookKeeper(new InvoiceFactory());
        clientData = new ClientData(Id.generate(), "client");
        invoiceRequest = new InvoiceRequest(clientData);

        taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.STANDARD, money1)).thenReturn(new Tax(new Money(0.23), "23%"));
        when(taxPolicy.calculateTax(ProductType.FOOD, money2)).thenReturn(new Tax(new Money(0.46), "46%"));

        productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.STANDARD);
        when(productData.getName()).thenReturn("product1");

        productData2 = mock(ProductData.class);
        when(productData2.getType()).thenReturn(ProductType.FOOD);
        when(productData2.getName()).thenReturn("product2");

        money1 = new Money(3);
        money2 = new Money(5);

    }

    @Test
    public void invoiceRequestWithOnePositionShouldReturnInvoiceWithOnePositionTest() {

        RequestItem requestItem = new RequestItem(productData, 5, money1);
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(equalTo(1)));

    }

    @Test
    public void invoiceRequestWithNoPositionsShouldReturnInvoiceWithZeroPositionsTest() {

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(equalTo(0)));

    }

    @Test
    public void invoiceGetItemsShouldReturnProperInformation() {

        RequestItem requestItem = new RequestItem(productData, 5,money1);
        RequestItem requestItem2 = new RequestItem(productData2, 6, money2);
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem2);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        List<String> productList = new ArrayList<>();
        productList.add("product1");
        productList.add("product2");

        List<ProductType> productTypeList = new ArrayList<>();
        productTypeList.add(ProductType.STANDARD);
        productTypeList.add(ProductType.FOOD);

        for (int i = 0; i < productList.size(); i++) {
            Assert.assertThat(invoice.getItems().get(i).getProduct().getName(), is(equalTo(productList.get(i))));
            Assert.assertThat(invoice.getItems().get(i).getProduct().getType(), is(equalTo(productTypeList.get(i))));
        }

    }

    @Test
    public void invoiceRequestWithTwoPositionsShouldCallCalculateTaxTwoTimesTest() {

        RequestItem requestItem1 = new RequestItem(productData, 5, money1);
        invoiceRequest.add(requestItem1);

        RequestItem requestItem2 = new RequestItem(productData2, 2, money2);
        invoiceRequest.add(requestItem2);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Mockito.verify(taxPolicy, times(1)).calculateTax(ProductType.STANDARD, money1);
        Mockito.verify(taxPolicy, times(1)).calculateTax(ProductType.FOOD, money2);

    }

    @Test
    public void invoiceRequestShouldCallProductDataGetTypeTwoTimesOnceForEveryProductTypeTest() {

        RequestItem requestItem1 = new RequestItem(productData, 5, money1);
        invoiceRequest.add(requestItem1);

        RequestItem requestItem2 = new RequestItem(productData2, 2, money2);
        invoiceRequest.add(requestItem2);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Mockito.verify(productData, times(1)).getType();
        Mockito.verify(productData2, times(1)).getType();

    }

    @Test
    public void invoiceRequestShouldCallRequestItemGetTotalCostTwoTimesTest() {


        RequestItem requestItem1 = mock(RequestItem.class);
        when(requestItem1.getTotalCost()).thenReturn(money1);
        when(requestItem1.getProductData()).thenReturn(productData);
        when(requestItem1.getQuantity()).thenReturn(5);
        invoiceRequest.add(requestItem1);
        invoiceRequest.add(requestItem1);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(requestItem1, times(2)).getTotalCost();

    }

}
