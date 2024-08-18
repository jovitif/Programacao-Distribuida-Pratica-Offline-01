package autenticacao;

import entity.Usuario;

@FunctionalInterface
public interface AcaoUsuarioAutenticado {
    void processar(Usuario usuario);
}
