attribute vec4 vPosition;
uniform mat4 vMatrix;
varying vec4 vColor;
void main(){
    gl_Position = vMatrix * vPosition;
    vColor = vec4(vPosition.x, vPosition.y, vPosition.z, 1f);
}