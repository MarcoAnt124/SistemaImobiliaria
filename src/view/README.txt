Criar um laço de repetição que mantenha o menu em funcionamento até que o usuário
escolha a opção "Sair"

Com a utilização da classe "Scanner" para a entrada de dados do usuário

Implemente uma mensagem de Boas Vindas e solicite ao usuário o ID(ou nome) do vendedor e a senha dele
e chame um método que será implementado no package "service" que ira verificar a autenticidade das informações

Dependendo do retorno dado do package "service" exiba uma mensagem de erro caso o login falhar e caso
o login for bem sucedido ir diretamente para o menu principal

Guardar as informações do vendedor no atributo "Vendedor" que está na classe "MenuPrincipal", este atributo
será usado posteriormente para a realização da venda

implemente estas funcionalidades ao menu de forma enumerada:
1.Gestão de Imóveis
    Submenu:
        1.Cadastrar Edificio
        2.Listar Apartamentos disponíveis
        3.Listar Apartamentos reservados
        4.Listar Apartamentos vendidos
        5.Sair

2.Gestão do Cliente
    Submenu:
        1.Cadastrar Cliente
        2.Buscar Cliente por CPF
        3.Buscar Cliente por nome
        4.Sair

3.Realizar Venda(com a utilização do metodo "fazerNegocio()" que será implementado no package "model")

4.Relatórios e Financeiros(exibir vendas realizadas)
