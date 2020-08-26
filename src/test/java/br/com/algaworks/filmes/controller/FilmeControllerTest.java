package br.com.algaworks.filmes.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import br.com.algaworks.filmes.model.Filme;
import br.com.algaworks.filmes.service.FilmeService;
import io.restassured.http.ContentType;

@WebMvcTest// anotação para dizer que é uma classe de teste
public class FilmeControllerTest {
	
	//todas os serviços relacionado a esse teste, tem que esta implementado nessa classe, como exemplo private FilmeService que está anotado com MockBean

	@Autowired
	private FilmeController filmeController;
	
	@MockBean//implementação falsa do componente
	private FilmeService filmeService;
	
	@BeforeEach//vai ocorrer antes de cada teste
	public void setup() {
		standaloneSetup(this.filmeController);// informa na hora que aplicação vai subir que só é pra testa as classes filmeController
	}
	
	@Test
	public void deveRetornarSucesso_QuandoBuscarFilme() {

		//isso é equivalente ao Assert.assertEquals(valorEsperado,função que faz a ação)
		when(this.filmeService.obterFilme(1L)) // when-mockito, essa funcionalidade espera a chamada do metodo em que se deseja fazer o teste
		// quando o metodo obterFilmes recebe 1, executa a ação abaixo,que é instancia um objeto do Tipo Filme
			.thenReturn(new Filme(1L, "O Poderoso Chefão", "Sem descrição"));
		
		given()
			.accept(ContentType.JSON)// informa o formato da aplicação
			
		.when()
			.get("/filmes/{codigo}", 1L)
		.then()
			.statusCode(HttpStatus.OK.value());
		/*
		 * o que esses comandos querem dizer
		 * dado a informação do contentType json quando o for feito uma requisão do tipo get em barra filmes, então o statusCode deve ser ok ou seja status 200
		*/
	}
	
	@Test
	public void deveRetornarNaoEncontrado_QuandoBuscarFilme() {
		
		when(this.filmeService.obterFilme(5L))// nesssa ocasião não existe filme com código 5
			.thenReturn(null);
		
		given()
			.accept(ContentType.JSON)
		.when()
			.get("/filmes/{codigo}", 5L)
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	public void deveRetornarBadRequest_QuandoBuscarFilme() {
		
		given()
			.accept(ContentType.JSON)
		.when()
			.get("/filmes/{codigo}", -1L)
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value());
		
		verify(this.filmeService, never()).obterFilme(-1L);
		/*medodo para garantir que a funcionalidade obterFilme não seja chamada
		 o never siginifica quando vezes utilizamos o metodo obterFilme, never "nunca", ou seja nunca chamamos esse método ness método
		 poderia colocar no lugar do never, outros valores.
		 verifique que a classe filmeService, nunca chame o metodo obterfilme, com o código -1l
		*/
	}
}
