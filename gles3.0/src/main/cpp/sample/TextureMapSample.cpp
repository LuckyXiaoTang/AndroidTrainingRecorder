//
// Created by Terry on 2020/5/9.
//

#include "TextureMapSample.h"


TextureMapSample::TextureMapSample() {
    m_TextrueId = 0;
}

TextureMapSample::~TextureMapSample() {
    NativeImageUtil::FreeNativeImage(&m_RenderImage);
}

void TextureMapSample::Init() {
    //生成一个纹理，将纹理 id 赋值给 m_TextureId
    glGenTextures(1, &m_TextrueId);
    //将纹理 m_TextureId 绑定到类型 GL_TEXTURE_2D 纹理
    glBindTexture(GL_TEXTURE_2D, m_TextrueId);
    //设置纹理 S 轴（横轴）的拉伸方式为截取
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    //设置纹理 T 轴（纵轴）的拉伸方式为截取
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    //设置纹理采样方式
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    glBindTexture(GL_TEXTURE_2D, GL_NONE);

    char vShaderStr[] =
            "#version 300 es                            \n"
            "layout(location = 0) in vec4 a_position;   \n"
            "layout(location = 1) in vec2 a_texCoord;   \n"
            "out vec2 v_texCoord;                       \n"
            "void main()                                \n"
            "{                                          \n"
            "   gl_Position = a_position;               \n"
            "   v_texCoord = a_texCoord;                \n"
            "}                                          \n";

    char fShaderStr[] =
            "#version 300 es                                  \n"
            "precision mediump float;                         \n"
            "in vec2 v_texCoord;                              \n"
            "layout(location = 0) out vec4 outColor;          \n"
            "uniform sampler2D s_TextureMap;                  \n"
            "void main()                                      \n"
            "{                                                \n"
            "   outColor = texture(s_TextureMap, v_texCoord); \n"
            "}                                                \n";


    m_ProgramObj = GLUtil::CreateProgram(vShaderStr, fShaderStr, m_VertexShader, m_FragmentShader);
    if (m_ProgramObj) {
        m_SamplerLoc = glGetUniformLocation(m_ProgramObj, "s_TextureMap");
    } else {
        LOGE(">>> TextureMapSample::Init create program fail <<<");
    }

}

// 加载图像数据、纹理坐标和顶点坐标数据，绘制实现纹理映射
void TextureMapSample::Draw(int width, int height) {
    LOGE(">>> TextureMapSample::Draw([w,h],[%d,%d]) <<<", width, height);
    if (m_ProgramObj == GL_NONE || m_TextrueId == GL_NONE)return;
    GLfloat vertexCoords[] = {
            -1.0f, 0.5f, 0.0f,      // Position 0 left - top
            1.0f, 0.5f, 0.0f,       // Position 1 right - top
            1.0f, -0.5f, 0.0f,        // Position 3 right - bottom
            -1.0f, -0.5f, 0.0f,       // Position 2 left - bottom

    };
    // 误：纹理坐标起始于(0, 0)，也就是纹理图片的左下角，终始于(1, 1)，即纹理图片的右上角
    // 手机中：纹理坐标起始于(0, 0)，也就是纹理图片的左上角，终始于(1, 1)，即纹理图片的右下角
    GLfloat textureCoords[] = {
            0.0f, 0.0f,        // Position 0 left - top
            1.0f, 0.0f,        // Position 1 right - top
            1.0f, 1.0f,         // Position 3 right - bottom
            0.0f, 1.0f        // Position 2 left - bottom
    };

    GLushort indices[] = {0, 1, 2, 1, 2, 3};

    //upload RGBA image data
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, m_TextrueId);
    //加载 RGBA 格式的图像数据
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, m_RenderImage.width, m_RenderImage.height, 0, GL_RGBA,
                 GL_UNSIGNED_BYTE, m_RenderImage.ppPlane[0]);
    glBindTexture(GL_TEXTURE_2D, GL_NONE);

    glUseProgram(m_ProgramObj);

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), vertexCoords);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 2 * sizeof(GLfloat), textureCoords);

    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    // Bind the RGBA map
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, m_TextrueId);

    // Set the RGBA map sampler to texture unit to 0
    glUniform1i(m_SamplerLoc, 0);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, indices);
}

void TextureMapSample::LoadImage(NativeImage *pImage) {
    LOGE(">>> TextureMapSample::LoadImage pImage = %p <<<", pImage->ppPlane[0]);
    m_RenderImage.width = pImage->width;
    m_RenderImage.height = pImage->height;
    m_RenderImage.format = pImage->format;
    NativeImageUtil::CopyNativeImage(pImage, &m_RenderImage);
}

void TextureMapSample::Destroy() {
    GLUtil::DeleteProgram(m_ProgramObj);
    glDeleteTextures(1, &m_TextrueId);
}
