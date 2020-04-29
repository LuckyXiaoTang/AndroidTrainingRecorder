//
// Created by Terry on 2020/4/29.
//

#include "TriangleSample.h"


TriangleSample::~TriangleSample() {
    if (m_ProgramObj) {
        glDeleteProgram(m_ProgramObj);
    }
}

void TriangleSample::Init() {
    char vShaderStr[] = "#version 300 es                         \n"
                        "layout(location = 0) in vec4 vPosition; \n"
                        "void main()                             \n"
                        "{                                       \n"
                        "    gl_position = vPosition;            \n"
                        "}                                       \n";

    char fShaderStr[] = "#version 300 es                         \n"
                        "precision mediump float;                \n"
                        "out vec4 fragColor;                     \n"
                        "{                                       \n"
                        "    fragColor = vec4(0.0,0.0,1.0,1.0);  \n"
                        "}                                       \n";
    m_ProgramObj = GLUtil::CreateProgram(vShaderStr, fShaderStr);
}
