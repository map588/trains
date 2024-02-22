const bool SWITCH_MAIN = false;
const bool SWITCH_ALT = true;
const bool LIGHT_RED = false;
const bool LIGHT_GREEN = true;
const bool CROSSING_CLOSED = false;
const bool CROSSING_OPEN = true;

bool maintenanceMode;
String PLCFile;
bool occupancyList[15];
bool switchStateList[15];
bool switchRequestedStateList[15];
bool trafficLightList[15];
bool crossingList [15];

void handleCommsInput(String input) {
  int splitIndex = input.indexOf('=');
  String variable = input.substring(0, splitIndex);
  String value = input.substring(splitIndex+1);

  if(variable == "runPLC") {
    runBlueLine();
  }
  else if(variable == "maintenanceMode") {
    maintenanceMode = value.substring(0,4) == "true";
    runBlueLine();
  }
  else if(variable == "occupancyList" || variable == "switchRequestedStateList") {
    int colonIndex = value.indexOf(':');
    int blockID = value.substring(0,colonIndex).toInt()-1;
    bool setValue = value.substring(colonIndex+1,colonIndex+5) == "true";

    if(variable == "occupancyList") {
      occupancyList[blockID] = setValue;
    }
    else {
      switchRequestedStateList[blockID] = setValue;
    }

    runBlueLine();
  }
}

void printBool(String prefix, bool boolVal) {
  Serial.print(prefix);
  Serial.println((boolVal ? "true" : "false"));
}

void sendTrackInfo() {
  printBool("switchStateList=5:",switchStateList[4]);
  printBool("trafficLightList=6:",trafficLightList[5]);
  printBool("trafficLightList=11:",trafficLightList[10]);
  printBool("crossingList=3:",crossingList[2]);
}

void runBlueLine() {
  if(!maintenanceMode) {
    // Process switch state requests
    if(switchStateList[4] != switchRequestedStateList[4]) {
      if(!occupancyList[4] && !occupancyList[5] && !occupancyList[10]) {
          switchStateList[4] = switchRequestedStateList[4];
      }
    }
  
    // Set traffic lights
    if(!occupancyList[0] && !occupancyList[1] && !occupancyList[2] && !occupancyList[3] && !occupancyList[4]) {
      if(switchStateList[4] == SWITCH_MAIN) {
        trafficLightList[5] = LIGHT_GREEN;
        trafficLightList[10] = LIGHT_RED;
      }
      else {
        trafficLightList[5] = LIGHT_RED;
        trafficLightList[10] = LIGHT_GREEN;
      }
    }
    else {
      trafficLightList[5] = LIGHT_RED;
      trafficLightList[10] = LIGHT_RED;
    }
  
    // Set Railroad Crossings
    if(occupancyList[1] || occupancyList[2] || occupancyList[3]) {
        crossingList[2] = CROSSING_CLOSED;
    }
    else {
        crossingList[2] = CROSSING_OPEN;
    }
  
    sendTrackInfo();
  }
}

void setup() {
  // put your setup code here, to run once:
  for(int i = 0; i < 15; i++) {
    occupancyList[i] = false;
    switchStateList[i] = false;
    switchRequestedStateList[i] = false;
    trafficLightList[i] = false;
    crossingList[i] = false;
  }
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  while(Serial.available() == 0) {}
  String serialString = Serial.readString();
  handleCommsInput(serialString);
}
