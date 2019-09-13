package com.bykea.pk.partner.tracking;

import java.util.List;

public interface Parser {
    List<Route> parse() throws RouteException;
}