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
#define P_A_CHUVA 2  
#define P_D_CHUVA 1

#define RFID 10

//Define os valores que serao usados no deslocamento
#define DESLOC_VIBRACAO 16
#define DESLOC_UMIDADE 8

void setup() {
  
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

  //Configurando sensor de chuva
  pinMode(P_D_CHUVA, INPUT);
  pinMode(P_A_CHUVA, INPUT);

  //velocidade de leitura na porta serial
  Serial.begin(9600);
}

long lerSensores(){
  long chuvaDigital = digitalRead(P_D_CHUVA);
  long chuvaAnalogico = analogRead(P_A_CHUVA);
  long umidade = analogRead(P_UMIDADE);
    
  //Para presenca o valor zero significa inatividade
  long vibracao = 0;
  
  //Pega o interruptor da deteccao de movimento
  byte interruptAcel = acel.getInterruptSource();
  //Se ocorrer uma interrupcao, a vibracao eh setado como true
  if(acel.triggered(interruptAcel, ADXL345_INT_ACTIVITY_BIT)){
    vibracao = 1;
  }
delay(1000);
Serial.println(" ");
Serial.print("chuva = ");
Serial.println(chuvaAnalogico);
Serial.print("umidade = ");
Serial.println(umidade);
Serial.print("vibracao = ");
Serial.print(vibracao);
  
  /*
  long info = (vibracao << DESLOC_VIBRACAO);///16
  info = info | (umidade << DESLOC_UMIDADE);//8
  info = info | chuvaAnalogico;
  return info;*/
}

void loop() {

lerSensores();
  //if(info != -1){    
      //Serial.print(info);
      //enviarParaUSB(info);//envia o long criado.
}

void enviarParaUSB(long info){
  char buff[sizeof(info)]={0};
  memcpy(&buff, &info, sizeof(info));
  Serial.write((uint8_t*) buff, sizeof(info));
}

//SIMULADOR

/*
long redeslocar(){
  long rf = 10;
  long chuva = 34;       //porcentagem
  long umidade = 56;    //porcentagem
  long vibracao = 1;   //o valor 1 significa que está ocorrendo vibracao

  
  
  info = info | (vibracao << DESLOC_VIBRACAO);///16
  info = info | (umidade << DESLOC_UMIDADE);//8
  info = info | chuva;
  return info;
}*/
