package com.example.formative_eduv4834254.data;

import java.util.List;

public class Trip {
    public long id;
    public String destination;
    public String notes;
    public String startDate;
    public String endDate;
    public String coverPhotoUri; // optional cover image for trip

    public List<ActivityItem> activities;
    public double visa;
    public double transport;
    public double meals;
    public double custom;

    public double subtotal;
    public double discount;
    public double total;

    public Trip() {}
}
