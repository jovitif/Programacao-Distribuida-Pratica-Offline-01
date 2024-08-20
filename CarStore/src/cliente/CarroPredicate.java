package cliente;

import entity.Carro;

@FunctionalInterface
interface CarroPredicate {
    boolean test(Carro carro, String criterio);
}