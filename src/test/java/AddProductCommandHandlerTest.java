import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
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
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;

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
    private Client client;
    private ClientRepository clientRepository;

    @Before
    public void setup() {
        AddProductCommandBuilder addProductCommandBuilder = new AddProductCommandBuilder();
        addProductCommandBuilder.withOrderId(new Id("1"));
        addProductCommandBuilder.withProductId(new Id("1"));
        addProductCommandBuilder.withQuantity(5);
        addProductCommand = addProductCommandBuilder.build();

        reservation = mock(Reservation.class);

        client = mock(Client.class);
        when(client.getId()).thenReturn(new Id("1"));

        clientRepository = mock(ClientRepository.class);
        when(clientRepository.load(new Id("1"))).thenReturn(client);

        product = mock(Product.class);
        when(product.isAvailable()).thenReturn(true);

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
    public void productIsAvaibleShouldBeCalledTwoTimesTest() {

        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);

        verify(product, times(2)).isAvailable();

    }

    @Test
    public void suggestionServiceSuggestEquivalentShouldBeCalledOneTime() {

        when(product.isAvailable()).thenReturn(false);
        when(suggestionService.suggestEquivalent(product, client)).thenReturn(product2);

        addProductCommandHandler.handle(addProductCommand);

        verify(suggestionService).suggestEquivalent(product, client);

    }

    @Test
    public void productIsAvaibleShouldReturnTrue() {

        Assert.assertTrue(product.isAvailable());

    }

    @Test
    public void ReservationRepositoryShouldReturnRepositoryWithIdOne() {

        Assert.assertEquals(reservation, reservationRepository.load(new Id("1")));

    }

}
