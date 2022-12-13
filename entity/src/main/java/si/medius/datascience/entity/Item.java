package si.medius.datascience.entity;

public class Item
{
    private String stockCode;
    private String description;
    private Integer quantity;
    private Double unitPrice;

    public String getStockCode()
    {
        return stockCode;
    }

    public void setStockCode(String stockCode)
    {
        this.stockCode = stockCode;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

    public Double getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public static Item fromCSVLine(String[] data)
    {
        Item item = new Item();
        item.setStockCode(data[1]);
        item.setDescription(data[2]);
        item.setQuantity(Integer.parseInt(data[3]));
        item.setUnitPrice(Double.parseDouble(data[5]));

        return item;
    }
}
