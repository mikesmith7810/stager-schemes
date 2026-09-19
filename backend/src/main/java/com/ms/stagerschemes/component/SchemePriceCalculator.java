package com.ms.stagerschemes.component;

import com.ms.stagerschemes.model.Scheme;
import com.ms.stagerschemes.model.SchemeRoom;
import com.ms.stagerschemes.model.SchemeRoomPack;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class SchemePriceCalculator {

  public BigDecimal calculateTotalPrice(Scheme scheme) {
    return scheme.getSchemeRooms().stream()
        .map(this::calculateSchemeRoomPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal calculateSchemeRoomPrice(SchemeRoom schemeRoom) {
    BigDecimal itemsTotal =
        schemeRoom.getSchemeRoomItems().stream()
            .map(
                schemeRoomItem ->
                    schemeRoomItem
                        .getItem()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(schemeRoomItem.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal packsTotal =
        schemeRoom.getSchemeRoomPacks().stream()
            .map(this::calculateSchemeRoomPackPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return itemsTotal.add(packsTotal);
  }

  private BigDecimal calculateSchemeRoomPackPrice(SchemeRoomPack schemeRoomPack) {
    return schemeRoomPack.getPack().getPackItems().stream()
        .map(
            packItem ->
                packItem.getItem().getPrice().multiply(BigDecimal.valueOf(packItem.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .multiply(BigDecimal.valueOf(schemeRoomPack.getQuantity()));
  }
}
