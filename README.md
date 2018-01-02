# OpenVR for Processing WIP
OpenVR Library for Processing, heavily under development, things ***will*** break

## Examples

### Tracking

<img src="img/map_vive.png?raw=true"/>

Tracking HTC Vive devices

<img src="img/map_rift.png?raw=true"/>

Tracking Oculus Rift devices

Call this method to get the pose matrix:
```
PMatrix3D GetDeviceToAbsoluteTrackingPose(int deviceId);
```
Use the utility function MathUtils.GetPosition to get the position from the matrix:
```
PVector GetPosition(PMatrix3D mat);
```

### Controller button events

<img src="img/controller_example.png?raw=true"/>

White: released

Gray: touched

Black: pressed


**Hand:** either Hand.LEFT or Hand.RIGHT (or Hand.INVALID).

**Button:** which button is pressed or touched, see details below
```
void buttonPressed(int hand, int button) {
...
}
```
```
void buttonReleased(int hand, int button) {
...
}
```
```
void buttonTouched(int hand, int button) {
...
}
```
```
void buttonUntouched(int hand, int button) {
...
}
```

### Polling
To get the state of the buttons, call the following functions in the draw loop:
```
boolean openVR.isButtonPressed(hand, button);
```
```
boolean openVR.isButtonTouched(hand, button);
```

## Controller Mapping - button constants
### Oculus
* **Grip:** k_EButton_Grip, k_EButton_Oculus_Grip
* **Trigger:** k_EButton_Axis1, k_EButton_Oculus_Trigger
* **A/X:** k_EButton_A
* **B/Y:** k_EButton_ApplicationMenu
* **Joystick:** k_EButton_Oculus_Joystick

### HTC Vive
Button codes:
* **Grip:** k_EButton_Grip
* **Trigger:** k_EButton_SteamVR_Trigger
* **Menu button:** k_EButton_ApplicationMenu
* **Touchpad:** k_EButton_SteamVR_Touchpad

### General
* **Grip:** k_EButton_Grip
* **Trigger:** k_EButton_Axis1
* **Button A:** k_EButton_A
* **Menu button:** k_EButton_ApplicationMenu
* **Joystick/Touchpad:** k_EButton_Axis0
