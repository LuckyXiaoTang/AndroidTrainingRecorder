attribute vec4 vPosition;
uniform mat4 vMatrix;
varying vec4 vColor;
void main(){
    gl_Position = vMatrix * vPosition;
//    if(vPosition.z!=0){
//        vColor = vec4(0f, 0f, 1f, 1f);
//    }else{
//        vColor = vec4(1f, 0f, 1f, 1f);
//    }
//    vColor = vec4(vPosition.x, vPosition.y, vPosition.z, 1f);
    float color;
    if(vPosition.z>0.0){
        color=vPosition.z;
    }else{
        color=-vPosition.z;
    }
    vColor=vec4(color,color,color,1.0);
}