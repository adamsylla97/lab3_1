package pl.com.bottega.ecommerce.sales.application.api.command;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

public class AddProductCommandBuilder {

    private Id orderId = Id.generate();
    private Id productId = Id.generate();
    private int quantity = 1;

    public AddProductCommandBuilder() {
    }

    public AddProductCommandBuilder withOrderId(Id id){
        this.orderId = id;
        return this;
    }

    public AddProductCommandBuilder withProductId(Id id){
        this.productId = id;
        return this;
    }

    public AddProductCommandBuilder withQuantity(int quantity){
        this.quantity = quantity;
        return this;
    }

    public AddProductCommand build(){
        return new AddProductCommand(orderId,productId,quantity);
    }

}
