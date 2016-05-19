/*
 * daemon.cpp
 *
 *
 * Leitor de  sensores que executa em background
 * no linux, Pode ser iniciado e parado
 * a partir de linha de comando
 *
 *
 *  Created on: 07/01/2016
 *      Author: Randler Ferraz
 */
#include <fcntl.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <stdlib.h>
#include <unistd.h>
#include <sys/stat.h>
#include <syslog.h>
#include <string.h>
#include <signal.h>

using namespace std;

#define ARQUIVO_LOG "/home/allexandre/arquivos_initd/winekeeper.log"
#define ARQUIVO_PIPE "/home/allexandre/arquivos_initd/winekeeper_p"

bool continuar = true;
int sensores = 0;
//alterar para a porta em que o arduino estiver
char* porta ="/dev/ttyACM0";
int hPorta=0;
bool portainiciada = false;
int resultado = EXIT_SUCCESS;

//---------------------------conectado--------------- DAEMONIZE ---------------------------------------------------
int daemonize() {
	int resultado = EXIT_SUCCESS;

	pid_t daemon_id = 0;
	pid_t sid = 0;

	// PASSO 1 : CRIAR UM PROCESSO A PARTIR DO MAIN
	daemon_id = fork();

	// SE FALHAR O ID DO PROCESSO É MENOR QUE 0
	if (daemon_id < 0) {
		cout << "Falha de criação do daemon!" << endl;
		// NÃO FAZ NADA, DESISTE DA EXECUCAO
		resultado = EXIT_FAILURE;
	} else {
		//  SE O DAEMON FOR CRIADO, FINALIZA O PROCESSO ORIGINAL
		// O TESTE CONDICIONAL ABAIXO GARANTE QUE SOMENTE SERA FINALIZADO O PROCESSO ORIGINAL
		if (daemon_id > 0) {
			cout << "Daemon criado! Saindo do processo principal." << endl;

			exit(0);
		}
		// CONFIGURA SESSÃO DE EXECUCAO DO DAEMON - START - STOP
		umask(0);

		sid = setsid();
		if (sid < 0) {
			exit(1);
		}

		// DESLIGA OS RECURSOS DE ENTRADA E SAIDA DE DADOS
		close(STDIN_FILENO);
		close(STDOUT_FILENO);
		close(STDERR_FILENO);
	}

	return resultado;
}

//------------------------------------------ FUNCAO DE PIPE ----------------------------------------------
bool pipeIniciado() {

	// CRIA O ARQUIVO DE PIPE
	// PERMISSÃO DE LEITURA E ESCRITA NO ARQUIVO
	int resultado = mkfifo(ARQUIVO_PIPE, 0x666);
	return (resultado != -1);
}

// ----------------------------------------- ENVIAR PARA PIPE -------------------------------------------------
int enviarParaPipe(int sensores) {
	int bytes = 0;

	//abrir o PIPE (abrir o arquivo)
	int apipe = open(ARQUIVO_PIPE, O_RDWR);

	if (apipe != -1) {
		//converter sensores inteiro para string
		//a integracao funciona melhor com strings
		stringstream ss;
		ss << sensores;
		string strSensores = ss.str();

		//escrever a string de sensores no PIPE
		bytes = write(apipe, strSensores.c_str(), strSensores.length());
	}
	//fechar o PIPE
	close(apipe);

	return bytes;
}

// ----------------------------------------- LER SENSORES -----------------------------
int lerSensores() {
	//sensores++;

	read(hPorta, (int*)&sensores, sizeof(sensores));
	usleep(100*1000);

	return sensores;
}

// ----------------------------------------- TRATAR SINAL ------------------------------------
void tratarSinal(int sinal) {
	//tratamento para os sinais de parada
	//do daemon (o daemon deve parar)
	switch (sinal) {
	case SIGHUP:
		continuar = false;
		break;
	case SIGTERM:
		continuar = false;
		break;
	case SIGQUIT:
		continuar = false;
		break;
	default:
		break;
	}

}

// ----------------------------------------- TRATAMENTO DE SINAIS ----------------------------
void iniciarTratamentoSinais() {
	//mapeamento entre sinal e funcao
	//de tratamento de sinais
	signal(SIGHUP, tratarSinal);
	signal(SIGTERM, tratarSinal);
	signal(SIGINT, tratarSinal);
	signal(SIGQUIT, tratarSinal);
}

//--------------------------- INICIAR CONEXÃO--------------------
void IniciarConexao(){
	hPorta  = open(porta, O_RDWR | O_NOCTTY | O_NDELAY);
			if (hPorta == -1){ // nao conseguiu pegar a porta retorna que falhou
				resultado = EXIT_FAILURE;
			} else { //configuraçao que precisa
				fcntl(hPorta, F_SETFL, 0);
				resultado = EXIT_SUCCESS;
			}

}

//------------------------------------------ MAIN ------------------------------------------------------
int main(int arg, char* argv[]) {

	//se for possivel criar o DAEMON
	if(daemonize() == EXIT_SUCCESS){
		//inicia-se o tratamento de sinais
		iniciarTratamentoSinais();
		//e o log para registro de operacoes
		ofstream log(ARQUIVO_LOG, ios_base::out | ios_base::app);
		log << "Arquivo de log iniciado!" << endl;

		log << "Pipe sendo criado" << endl;
		if(!pipeIniciado()){
			log << "Falha na inicializacao do PIPE" << endl;
			exit(1);
		}

		//executa em um loop interrompivel...
		continuar = true;
		while(continuar){

			IniciarConexao();
			//a leitura dos sensores
			int sensores = lerSensores();

			if(resultado == 0){
				log << "Enviando sensores: " << sensores << endl;
				//e o envio para o PIPE
				enviarParaPipe(sensores);
				sleep(3);
			} else {
				log << "Problemas ao conectar com a porta."<<endl;
			}
		}

		//remove o arquivo do PIPE apos uso/finalizacao
		unlink(ARQUIVO_PIPE);

		log << "Daemon de sensoriamento finalizado." << endl;
	}

	return 0;
}

// startar pela linha de comando -> CAMINHO   chmod +x init.d
// ./init.d status
// ./init.d start
// tail -f CAMINHO_SALVO/sensoriamentod.log
