//
// Created by Terry on 2020/4/29.
//

#include "TriangleSample.h"

TriangleSample::TriangleSample() {

}

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
                        "    gl_Position = vPosition;            \n"
                        "}                                       \n";

    char fShaderStr[] = "#version 300 es                               \n"
                        "precision mediump float;                      \n"
                        "out vec4 fragColor;                           \n"
                        "void main()                                   \n"
                        "{                                             \n"
                        "    fragColor = vec4 ( 1.0, 0.0, 0.0, 1.0 );  \n"
                        "}                                             \n";
    m_ProgramObj = GLUtil::CreateProgram(vShaderStr, fShaderStr, m_VertexShader, m_FragmentShader);
}

void TriangleSample::Draw(int width, int height) {
    LOGE(">>> TriangleSample::Draw <<<");
    GLfloat vVertices[] = {
            0.0f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
    };
    if (m_ProgramObj == 0) return;
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glUseProgram(m_ProgramObj);

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, vVertices);
    glEnableVertexAttribArray(0);
    glDrawArrays(GL_TRIANGLES, 0, 3);
}

void TriangleSample::Destroy() {
    LOGE(">>> TriangleSample::Destroy <<<");
    GLUtil::DeleteProgram(m_ProgramObj);
}