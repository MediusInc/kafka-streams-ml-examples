package si.medius.makeit.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Invoice
{
    private String invoiceNo;
    private String customerID;
    private String country;
    private LocalDateTime invoiceDate;
    private List<Item> items;

    private InvoiceStatus status;

    public Invoice()
    {
        status = InvoiceStatus.PENDING;
    }

    public String getInvoiceNo()
    {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo)
    {
        this.invoiceNo = invoiceNo;
    }

    public String getCustomerID()
    {
        return customerID;
    }

    public void setCustomerID(String customerID)
    {
        this.customerID = customerID;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public LocalDateTime getInvoiceDate()
    {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate)
    {
        this.invoiceDate = invoiceDate;
    }

    public List<Item> getItems()
    {
        return items;
    }

    public void setItems(List<Item> items)
    {
        this.items = items;
    }

    public InvoiceStatus getStatus()
    {
        return status;
    }

    public void setStatus(InvoiceStatus status)
    {
        this.status = status;
    }

    public void addItem(Item item) {
        if(items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
    }

    public Double getInvoicePrice() {
        if(items != null) {
            Double price = 0.0;
            for(var item : items) {
                price += item.getUnitPrice() * item.getQuantity();
            }
            return price;
        }
        return 0.0;
    }

    public static Invoice fromCSVLine(String[] data) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(data[0]);
        invoice.setCustomerID(data[6].replace(".0", ""));
        invoice.setCountry(data[7]);
        invoice.setInvoiceDate(LocalDateTime.parse(data[4].replace("Z", "")).plusYears(10));

        return invoice;
    }
}
