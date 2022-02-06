package se.liu.tdp028_pocketpantry.Models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Map;

public class Ingredient implements Serializable {
    public enum Unit {
        SPICE {
            @Override
            public float getWeight() {
                return 1.0f;
            }

            @NonNull
            @Override
            public String toString() {
                return "spice";
            }
        },
        ML {
            @Override
            public float getWeight() {
                return 1.0f;
            }

            @NonNull
            @Override
            public String toString() {
                return "ml";
            }
        },
        CL {
            @Override
            public float getWeight() {
                return 10.0f;
            }

            @NonNull
            @Override
            public String toString() {
                return "cl";
            }
        },
        DL {
            @Override
            public float getWeight() {
                return 100.0f;
            }

            @NonNull
            @Override
            public String toString() {
                return "dl";
            }
        },
        L {
            @Override
            public float getWeight() {
                return 1000.0f;
            }

            @NonNull
            @Override
            public String toString() {
                return "l";
            }
        },
        G {
            @Override
            public float getWeight() {
                return 1.0f;
            }

            @NonNull
            @Override
            public String toString() {
                return "g";
            }
        },
        KG {
            @Override
            public float getWeight() {
                return 1000.0f;
            }

            @NonNull
            @Override
            public String toString() {
                return "kg";
            }
        },
        PCS {
            @Override
            public float getWeight() {
                return 1.0f;
            }

            @NonNull
            @Override
            public String toString() {
                return "pcs";
            }
        },
        TSP {
            @Override
            public float getWeight() {
                return 5.0f;
            }

            @NonNull
            @Override
            public String toString() {
                return "tsp";
            }
        },
        TBSP {
            @Override
            public float getWeight() {
                return 15.0f;
            }

            @NonNull
            @Override
            public String toString() {
                return "tbsp";
            }
        };

        public float getWeight() {
            return -1;
        }

        public double convert(Unit to, double amount) {
            return (amount * this.getWeight()) / to.getWeight();
        }

        public static Unit fromString(String s) {
            s = s.toUpperCase();
            Unit unitConverted;
            if (s.equals("SPICE")) {
                unitConverted = SPICE;
            } else {
                unitConverted = Unit.valueOf(s.toUpperCase());
            }
            return unitConverted;
        }
    }

    private String name;

    private double amount;
    private Unit unit;
    @Override
    public String toString() {
        return amount + " " + unit + " " + name;
    }

    public Ingredient(String name, double amount, Unit unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    public Ingredient(Map<String, Object> ingredient) {
        name = (String)ingredient.get("name");
        amount = (double)ingredient.get("amount");
        Unit unitConverted = Unit.fromString(ingredient.get("unit").toString());
        unit = unitConverted;

    }
    public double convert(Unit from, Unit to, double amount) {
        return (amount * from.getWeight()) / to.getWeight();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmountToString() {
        String amountToString = amount + "";
        return amountToString;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {

        this.unit = unit;
    }



}




