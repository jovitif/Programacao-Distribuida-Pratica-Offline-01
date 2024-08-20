package autenticacao;

import entity.Usuario;

@FunctionalInterface
public interface FiltroUsuario {
    boolean filtrar(Usuario usuario);
}
