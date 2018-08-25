package com.seanschlaefli.medstat.database;

public class MedStatDbSchema {

    public static final class MedicalItemTable {
        public static final String NAME = "medical_item";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String VALUE = "value";
            public static final String DATETIME = "datetime";

        }
    }

    public static final class MedicalUnitsTable {

        public static final String NAME = "medical_item_units";

        public static final class Cols {
            public static final String NAME = "name";
            public static final String UNITS = "units";
        }
    }
}
