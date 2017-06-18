#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in float id;

out vec3 blockPos;
flat out float surfaceID;

uniform mat4 worldMatrix;
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * worldMatrix * vec4(position, 1.0);
    blockPos = position;
    surfaceID = id;
}
