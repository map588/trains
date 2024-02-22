void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void handleInput(String input) {
  int splitIndex = input.indexOf('=');
  String variable = input.substring(0, splitIndex);
  String value = input.substring(splitIndex+1);

  if(variable == "maintenanceMode") {
    Serial.print("maintenanceMode set to ");
    Serial.println(value);
  }
}

void loop() {
  // put your main code here, to run repeatedly:
  while(Serial.available() == 0) {}
  String serialString = Serial.readString();
  handleInput(serialString);
}
