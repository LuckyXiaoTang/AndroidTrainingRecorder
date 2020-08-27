//
// Created by Terry on 2020/7/10.
//

#include "NV21TextureMapSample.h"

void NV21TextureMapSample::LoadImage(NativeImage *pImage) {
    LOGE("NV21TextureMapSample::LoadImage pImage = %p", pImage);
    if (pImage) {
        m_RenderImage.width = pImage->width;
        m_RenderImage.height = pImage->height;
        m_RenderImage.format = pImage->format;
        NativeImageUtil::CopyNativeImage(pImage, &m_RenderImage);
    }
}

void NV21TextureMapSample::Init() {
    LOGE(">>> NV21TextureMapSample::Init <<<");
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
            "#version 300 es                                      \n"
            "precision mediump float;                             \n"
            "in v_texCoord;                                       \n"
            "layout(location = 0) out vec4 outColor;              \n"
            "uniform sampler2D y_texture;                         \n"
            "uniform sampler2D uv_texture;                        \n"
            "void main()                                          \n"
            "{                                                    \n"
            "   vec3 yuv;                                         \n"
            "   yuv.x = texture(y_texture,v_texCoord).r;          \n"
            "   yuv.y = texture(uv_texture,v_texCoord).r - 0.5;   \n"
            "   yuv.z = texture(uv_texture,v_texCoord).r - 0.5;   \n"
            "   vec3 rgb = mat3(1,1,1,                            \n"
            "                   0,-0.344,1.770,                   \n"
            "                   1.403,-0.714,0)*yuv;              \n"
            "   outColor = vec4(rgb,1);                           \n"
            "}                                                    \n";

    m_ProgramObj = GLUtil::CreateProgram(vShaderStr, fShaderStr, m_VertexShader, m_FragmentShader);

    m_SampplerLoc_Y = glGetUniformLocation(m_ProgramObj, "y_texture");
    m_SampplerLoc_UV = glGetUniformLocation(m_ProgramObj, "uv_texture");

    GLuint textureIds[] = {0, 0};
    glGenTextures(2, textureIds);

    m_TextureId_Y = textureIds[0];
    m_TextureId_UV = textureIds[1];
}

void NV21TextureMapSample::Draw(int screenW, int screenH) {
    LOGE("NV21TextureMapSample::Draw screenW = %d, screenH = %d", screenW, screenH);
    if (m_ProgramObj == GL_NONE || m_TextureId_Y == GL_NONE || m_TextureId_UV == GL_NONE) {
        //upload Y plane data
        glBindTexture(GL_SAMPLER_2D, m_TextureId_Y);
        glTexImage2D(GL_SAMPLER_2D, 0, GL_LUMINANCE, m_RenderImage.width, m_RenderImage.height, 0,
                     GL_LUMINANCE, GL_UNSIGNED_BYTE, m_RenderImage.ppPlane[0]);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_SAMPLER_2D, GL_NONE);

        //upload UV plane data
        glBindTexture(GL_SAMPLER_2D, m_TextureId_UV);
        glTexImage2D(GL_SAMPLER_2D, 0, GL_LUMINANCE_ALPHA, m_RenderImage.width >> 1,
                     m_RenderImage.height >> 1, 0,
                     GL_LUMINANCE_ALPHA, GL_UNSIGNED_BYTE, m_RenderImage.ppPlane[1]);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_SAMPLER_2D, GL_NONE);

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

        glUseProgram(m_ProgramObj);

        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), vertexCoords);
        glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), textureCoords);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);



    }
}

void NV21TextureMapSample::Destroy() {

}
