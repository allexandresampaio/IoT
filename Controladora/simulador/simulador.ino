#include "RCSwitch.h"
#include "Wire.h"
#include "ADXL345.h"

//Instancia o emissor e o receptor
RCSwitch emissor = RCSwitch();
RCSwitch receptor = RCSwitch();

//Instancia o acelerometro
ADXL345 acel = ADXL345();

//Define os pinos dos sensores
#define P_UMIDADE 0
#define P_CHUVA 1  //
#define P_VIBRACAO 3

#define RFID 10

//Define os valores que serao usados no deslocamento
#define DESLOC_RFID 17
#define DESLOC_VIBRACAO 16
#define DESLOC_UMIDADE 8

void setup() {
  Serial.begin(9600);
  //Configuracao do emissor/Receptor RF
  emissor.enableTransmit(4);
  receptor.enableReceive(0);//Pega o primeiro pino de interrupcao

  //Configuracao do acelerometro para deteccao de vibracao
  acel.powerOn();
  //Define INATIVIDADE (repouso)
  acel.setInactivityX(1); 
  acel.setInactivityY(1);
  acel.setInactivityZ(1);
  //Define ATIVIDADE (vibracao)
  acel.setActivityX(1); 
  acel.setActivityY(1);
  acel.setActivityZ(1);

  //Acionamento da deteccao de atividade e inatividade
  acel.setInterruptMapping(ADXL345_INT_ACTIVITY_BIT, ADXL345_INT1_PIN);
  acel.setInterruptMapping(ADXL345_INT_INACTIVITY_BIT, ADXL345_INT1_PIN);
  acel.setInterrupt(ADXL345_INT_ACTIVITY_BIT, 1);
  acel.setInterrupt(ADXL345_INT_INACTIVITY_BIT, 1);

  //calibracao  de sensibilidade em relacao aa atividade e inatividade
  acel.setActivityThreshold(50);
  acel.setInactivityThreshold(50);
  acel.setTimeInactivity(10);
}

long lerSensoresRF(){
  long rf = RFID;
  long chuva = digitalRead(P_CHUVA);
  long umidade = digitalRead(P_UMIDADE);
    
  //Para presenca o valor zero significa inatividade
  long vibracao = 0;
  
  //Pega o interruptor da deteccao de movimento
  byte interruptAcel = acel.getInterruptSource();
  //Se ocorrer uma interrupcao, a vibracao eh setado como true
  if(acel.triggered(interruptAcel, ADXL345_INT_ACTIVITY_BIT)){
    vibracao = 1;
  }

  long info = rf << DESLOC_RFID;//17
  info = info | (vibracao << DESLOC_VIBRACAO);///16
  info = info | (umidade << DESLOC_UMIDADE);//8
  info = info | chuva;
  return info;
}

void emitir(long info){
  emissor.send(info, 32);
  
}

long receber(){
  long info = -1;
  if(receptor.available()){ //se ha dados disponiveis
    info = receptor.getReceivedValue();
    receptor.resetAvailable(); //zera o receptor
  }
  return info;
}

void loop() {
// emissao de dados
//  emitir(simularSensoresRF()); //trocar por lerSensoresRF
  
//recepcao de dados
//  long info = receber();

long info = simularSensoresRF();
  if(info != -1){
    if (extrairRFID(info) == 10){
      enviarParaUSB(info);//envia o long criado.
    }
  }
  //Delay de 3 segundos para sincronizar com o sensor de presenca
  delay(3000);
}

int extrairRFID(long info){
  int rf = info >> DESLOC_RFID;//18
  return rf;
}

void enviarParaUSB(long info){
  char buff[sizeof(info)]={0};
  memcpy(&buff, &info, sizeof(info));
  Serial.write((uint8_t*) buff, sizeof(info));
}

//SIMULADOR

long simularSensoresRF(){
  long rf = 12;
  long chuva = 34;       //porcentagem
  long umidade = 56;    //porcentagem
  long vibracao = 1;   //o valor 1 significa que está ocorrendo vibracao
  
  long info = rf << DESLOC_RFID;//17
  info = info | (vibracao << DESLOC_VIBRACAO);///16
  info = info | (umidade << DESLOC_UMIDADE);//8
  info = info | chuva;
  return info;
}
