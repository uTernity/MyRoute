#version 330 core
out vec4 FragColor;

in vec2 texCoords;

uniform vec4 vertexColor;
uniform sampler2D texture2D;

void main()
{
    FragColor = texture(texture2D, texCoords);
}