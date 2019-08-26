package com.bykea.pk.partner.map.tracking;

import java.util.List;

public interface Parser {
    List<Route> parse() throws RouteException;
}