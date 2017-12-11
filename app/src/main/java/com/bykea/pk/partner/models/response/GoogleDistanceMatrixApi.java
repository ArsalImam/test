package com.bykea.pk.partner.models.response;

import org.apache.commons.lang3.StringUtils;

public class GoogleDistanceMatrixApi {
    private String status;

    private String[] destination_addresses;

    private String[] origin_addresses;

    private Rows[] rows;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getDestination_addresses() {
        return destination_addresses;
    }

    public void setDestination_addresses(String[] destination_addresses) {
        this.destination_addresses = destination_addresses;
    }

    public String[] getOrigin_addresses() {
        return origin_addresses;
    }

    public void setOrigin_addresses(String[] origin_addresses) {
        this.origin_addresses = origin_addresses;
    }

    public Rows[] getRows() {
        return rows;
    }

    public void setRows(Rows[] rows) {
        this.rows = rows;
    }

    public class Rows {
        private Elements[] elements;

        public Elements[] getElements() {
            return elements;
        }

        public void setElements(Elements[] elements) {
            this.elements = elements;
        }

    }

    public class Elements {
        private Duration duration;

        private Distance distance;

        private String status;

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public Distance getDistance() {
            return distance;
        }

        public void setDistance(Distance distance) {
            this.distance = distance;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "ClassPojo [duration = " + duration + ", distance = " + distance + ", status = " + status + "]";
        }

        public class Duration {
            private String text;

            private String value;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getValue() {
                return value;
            }

            public int getValueInt() {
                return StringUtils.isNotBlank(value) ? Integer.parseInt(value) : 0;
            }

            public void setValue(String value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return "ClassPojo [text = " + text + ", value = " + value + "]";
            }
        }

        public class Distance {
            private String text;

            private String value;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getValue() {
                return value;
            }

            public int getValueInt() {
                return StringUtils.isNotBlank(value) ? Integer.parseInt(value) : 0;
            }

            public void setValue(String value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return "ClassPojo [text = " + text + ", value = " + value + "]";
            }
        }

    }
}
