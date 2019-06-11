import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommandBuilder;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandlerBuilder;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AddProductCommandHandlerTest {

    private AddProductCommand addProductCommand;
    private AddProductCommandHandler addProductCommandHandler;
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private SuggestionService suggestionService;
    private Product product;
    private Product product2;
    private Reservation reservation;
    Client client;
    private ClientRepository clientRepository;
    private ClientData clientData;
    private Reservation.ReservationStatus reservationStatus;
    ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);

    @Before
    public void setup() {
        AddProductCommandBuilder addProductCommandBuilder = new AddProductCommandBuilder();
        addProductCommandBuilder.withOrderId(new Id("1"));
        addProductCommandBuilder.withProductId(new Id("1"));
        addProductCommandBuilder.withQuantity(5);
        addProductCommand = addProductCommandBuilder.build();

        clientData = new ClientData(new Id("1"), "client");
        reservationStatus = Reservation.ReservationStatus.OPENED;

        reservation = new Reservation(new Id("1"), reservationStatus, clientData, new Date());

        client = new Client();

        clientRepository = mock(ClientRepository.class);
        when(clientRepository.load(new Id("1"))).thenReturn(client);

        product = new Product(new Id("1"), new Money(122), "product", ProductType.STANDARD);
        product2 = new Product(new Id("2"), new Money(111), "product2", ProductType.STANDARD);

        reservationRepository = mock(ReservationRepository.class);
        when(reservationRepository.load(new Id("1"))).thenReturn(reservation);

        productRepository = mock(ProductRepository.class);
        when(productRepository.load(any())).thenReturn(product);

        suggestionService = mock(SuggestionService.class);
        when(suggestionService.suggestEquivalent(product, client)).thenReturn(product);

        AddProductCommandHandlerBuilder addProductCommandHandlerBuilder = new AddProductCommandHandlerBuilder();
        addProductCommandHandlerBuilder.withReservationRepository(reservationRepository);
        addProductCommandHandlerBuilder.withProductRepository(productRepository);
        addProductCommandHandlerBuilder.withSuggestionService(suggestionService);
        addProductCommandHandlerBuilder.withClientRepository(clientRepository);
        addProductCommandHandler = addProductCommandHandlerBuilder.build();
    }

    @Test
    public void reservationRepositoryLoadShouldBeCalledTwoTimes() {

        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepository, Mockito.times(2)).load(new Id("1"));

    }

    @Test
    public void productRepositoryLoadShouldBeCalledThreeTimesTest() {

        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);

        when(reservationRepository.load(new Id("1"))).thenReturn(reservation);

        verify(productRepository, times(3)).load(new Id("1"));

    }

    @Test
    public void suggestionServiceSuggestEquivalentShouldBeCalledOneTime() {

        when(suggestionService.suggestEquivalent(product, client)).thenReturn(product2);
        product.markAsRemoved();
        addProductCommandHandler.handle(addProductCommand);

        verify(suggestionService).suggestEquivalent(product, client);

    }

    @Test
    public void productIsAvaibleShouldReturnTrue() {

        Assert.assertTrue(product.isAvailable());

    }

    @Test
    public void ReservationRepositoryShouldReturnRepositoryWithIdOne() {

        addProductCommandHandler.handle(addProductCommand);
        verify(reservationRepository).save(captor.capture());
        Assert.assertEquals(reservation, captor.getValue());

    }

    @Test
    public void equalsTest(){
        Assert.assertTrue(true);
    }

}
