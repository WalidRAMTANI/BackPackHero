package Rooms;

import java.util.Objects;

import model.Merchant;

public record MerchantRoom(Merchant merchant) implements RoomType {

    /**
     * Returns the type name of this room.
     *
     * @return the string "MERCHANT" to identify this room type
     */

    public MerchantRoom {
        Objects.requireNonNull(merchant);
    }

    public MerchantRoom() {
        // TODO Auto-generated constructor stub
        this(generateMerchant());
    }

    static Merchant generateMerchant() {
        return new Merchant();
    }

    @Override
    public String getTypeName() {
        return "MERCHANT";
    }

}
