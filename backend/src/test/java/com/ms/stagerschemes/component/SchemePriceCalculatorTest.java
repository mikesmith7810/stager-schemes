package com.ms.stagerschemes.component;

import static org.assertj.core.api.Assertions.assertThat;

import com.ms.stagerschemes.model.Item;
import com.ms.stagerschemes.model.Pack;
import com.ms.stagerschemes.model.PackItem;
import com.ms.stagerschemes.model.Scheme;
import com.ms.stagerschemes.model.SchemeRoom;
import com.ms.stagerschemes.model.SchemeRoomItem;
import com.ms.stagerschemes.model.SchemeRoomPack;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchemePriceCalculatorTest {

  private SchemePriceCalculator schemePriceCalculator;

  @BeforeEach
  void setUp() {
    schemePriceCalculator = new SchemePriceCalculator();
  }

  @Test
  void calculateTotalPrice_withNoRooms_returnsZero() {
    Scheme scheme = new Scheme("Empty Scheme");

    BigDecimal totalPrice = schemePriceCalculator.calculateTotalPrice(scheme);

    assertThat(totalPrice).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  void calculateTotalPrice_withItemsOnly_returnsSumOfItemLineTotals() {
    Item chair = new Item("Chair", new BigDecimal("150.00"), null, null);
    Item lamp = new Item("Lamp", new BigDecimal("50.00"), null, null);
    Scheme scheme = new Scheme("Scheme");
    SchemeRoom schemeRoom = new SchemeRoom(scheme, null, "Living Room");
    schemeRoom.getSchemeRoomItems().add(new SchemeRoomItem(schemeRoom, chair, 2));
    schemeRoom.getSchemeRoomItems().add(new SchemeRoomItem(schemeRoom, lamp, 1));
    scheme.getSchemeRooms().add(schemeRoom);

    BigDecimal totalPrice = schemePriceCalculator.calculateTotalPrice(scheme);

    assertThat(totalPrice).isEqualByComparingTo(new BigDecimal("350.00"));
  }

  @Test
  void calculateTotalPrice_withPacksOnly_returnsSumOfPackItemLineTotals() {
    Item sofa = new Item("Sofa", new BigDecimal("300.00"), null, null);
    Pack pack = new Pack("Living Room Pack");
    pack.getPackItems().add(new PackItem(pack, sofa, 1));
    Scheme scheme = new Scheme("Scheme");
    SchemeRoom schemeRoom = new SchemeRoom(scheme, null, "Living Room");
    schemeRoom.getSchemeRoomPacks().add(new SchemeRoomPack(schemeRoom, pack, 1));
    scheme.getSchemeRooms().add(schemeRoom);

    BigDecimal totalPrice = schemePriceCalculator.calculateTotalPrice(scheme);

    assertThat(totalPrice).isEqualByComparingTo(new BigDecimal("300.00"));
  }

  @Test
  void calculateTotalPrice_withMultipleRooms_sumsPricesAcrossAllRooms() {
    Item chair = new Item("Chair", new BigDecimal("100.00"), null, null);
    Scheme scheme = new Scheme("Scheme");

    SchemeRoom bedroomRoom = new SchemeRoom(scheme, null, "Bedroom");
    bedroomRoom.getSchemeRoomItems().add(new SchemeRoomItem(bedroomRoom, chair, 2));

    SchemeRoom livingRoomRoom = new SchemeRoom(scheme, null, "Living Room");
    livingRoomRoom.getSchemeRoomItems().add(new SchemeRoomItem(livingRoomRoom, chair, 3));

    scheme.getSchemeRooms().add(bedroomRoom);
    scheme.getSchemeRooms().add(livingRoomRoom);

    BigDecimal totalPrice = schemePriceCalculator.calculateTotalPrice(scheme);

    assertThat(totalPrice).isEqualByComparingTo(new BigDecimal("500.00"));
  }

  @Test
  void calculateSchemeRoomPrice_withItemsAndPacks_returnsCombinedTotal() {
    Item chair = new Item("Chair", new BigDecimal("100.00"), null, null);
    Item table = new Item("Table", new BigDecimal("200.00"), null, null);
    Pack pack = new Pack("Furniture Pack");
    pack.getPackItems().add(new PackItem(pack, table, 1));

    SchemeRoom schemeRoom = new SchemeRoom(null, null, "Dining Room");
    schemeRoom.getSchemeRoomItems().add(new SchemeRoomItem(schemeRoom, chair, 4));
    schemeRoom.getSchemeRoomPacks().add(new SchemeRoomPack(schemeRoom, pack, 1));

    BigDecimal roomPrice = schemePriceCalculator.calculateSchemeRoomPrice(schemeRoom);

    assertThat(roomPrice).isEqualByComparingTo(new BigDecimal("600.00"));
  }

  @Test
  void calculateSchemeRoomPrice_packWithMultipleItems_sumsAllPackItemCosts() {
    Item chair = new Item("Chair", new BigDecimal("50.00"), null, null);
    Item cushion = new Item("Cushion", new BigDecimal("20.00"), null, null);
    Pack pack = new Pack("Chair Set");
    pack.getPackItems().add(new PackItem(pack, chair, 2));
    pack.getPackItems().add(new PackItem(pack, cushion, 4));

    SchemeRoom schemeRoom = new SchemeRoom(null, null, "Lounge");
    schemeRoom.getSchemeRoomPacks().add(new SchemeRoomPack(schemeRoom, pack, 1));

    BigDecimal roomPrice = schemePriceCalculator.calculateSchemeRoomPrice(schemeRoom);

    assertThat(roomPrice).isEqualByComparingTo(new BigDecimal("180.00"));
  }
}
