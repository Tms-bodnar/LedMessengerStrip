
#include <SoftwareSerial.h>
#include <MD_Parola.h>
#include <MD_MAX72xx.h>
#include <SPI.h>
#include "Parola_Fonts_data.h"

#define USE_UI_CONTROL 0

#define HARDWARE_TYPE MD_MAX72XX::ICSTATION_HW
#define MAX_DEVICES 8
#define CLK_PIN 13
#define DATA_PIN 11
#define CS_PIN 10
#define RX_PIN 0
#define TX_PIN 1

SoftwareSerial BTserial(RX_PIN, TX_PIN);
MD_Parola P = MD_Parola(HARDWARE_TYPE, DATA_PIN, CLK_PIN, CS_PIN, MAX_DEVICES);

uint8_t scrollSpeed = 60; // default frame delay value
textEffect_t scrollEffect = PA_SCROLL_LEFT;
textPosition_t scrollAlign = PA_LEFT;
uint16_t scrollPause = 100; // in milliseconds
textEffect_t textFade = PA_FADE;
textPosition_t fadedTextPosition = PA_CENTER;

// Global message buffers shared by Serial and Scrolling functions
#define BUF_SIZE 75
char advMessage[BUF_SIZE] = {"Booting..."};
char curMessage[BUF_SIZE] = {""};
char newMessage[BUF_SIZE] = {""};
bool newMessageAvailable = false;
String readString;
int repeat = 0;

void readSerial(void)
{
  String readString;
  while (BTserial.available())
  {
    delay(100);
    char cp = (char)BTserial.read();
    readString += cp;
  }
  if (readString.length() > 0)
  {
    strcpy(newMessage, readString.c_str());
    newMessageAvailable = true;
    repeat = 0;
  }
  readString = "";
}
void setup()
{
  Serial.begin(9600);
  Serial.println("Arduino is ready");
  BTserial.begin(9600);
  P.begin();
  P.setFont(ExtASCII);
  P.setIntensity(0);
  strcpy(curMessage, advMessage);
  Serial.println("len:" + strlen(curMessage));
  if (strlen(curMessage) > 10)
  {
    P.displayText(curMessage, scrollAlign, scrollSpeed, scrollPause, scrollEffect, scrollEffect);
  }
  else
  {
    P.displayText(curMessage, fadedTextPosition, 300, 100, textFade, textFade);
  }
}

void loop()
{
  if (repeat < 3)
  {
    if (P.displayAnimate())
    {
      P.displayReset();
      if (newMessageAvailable)
      {
        strcpy(curMessage, newMessage);
        newMessageAvailable = false;
      }
      repeat++;
    }
  }
  readSerial();
}
