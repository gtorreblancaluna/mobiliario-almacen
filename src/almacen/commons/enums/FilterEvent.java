package almacen.commons.enums;

public enum FilterEvent {
        
        CUSTOMER("customer","cliente"),
        SYSTEM_DATE("systemDate","fecha sistema"),
        LIMIT("limit","limite"),
        DRIVER_ID("driverId","chofer"),
        TYPE("type","tipo evento"),
        STATUS("statusId","estado evento"),
        INIT_DELIVERY_DATE("initDeliveryDate","fecha entrega evento"),
        END_DELIVERY_DATE("endDeliveryDate","fecha devolucion evento"),
        INIT_CREATED_DATE("initCreatedDate","fecha inicio elaboracion"),
        END_CREATED_DATE("endCreatedDate","fecha fin elaboracion"),
        INIT_EVENT_DATE("initEventDate","fecha inicio evento"),
        END_EVENT_DATE("endEventDate","fecha fin evento"),
        FILTER_BY_CATEGORY_USER("filterByCategoryUser",""),
        USER_ID("userId","encargado"),
        ATTEND_TYPE("attendType","tipo atendido"),
        FOLIO("folio","folio");
        
        FilterEvent (String key, String description) {
            this.key = key;
            this.description = description;
        }
        
        private final String key;
        private final String description;

        public String getDescription() {
            return description;
        }
        
        
        
        public String getKey(){
            return key;
        }
    }
