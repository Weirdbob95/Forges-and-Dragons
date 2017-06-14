#version 330 core

in vec3 blockPos;

out vec4 color;

uniform sampler2D texture_sampler;
uniform vec3 normal;

const int SIDE_LENGTH = 32;

void main() {
    vec3 pos = floor(blockPos - .5 * normal);
    vec2 samplePos = vec2(pos.z/SIDE_LENGTH + pos.y, pos.x) / SIDE_LENGTH;
    float shade = 1 + (dot(normal, vec3(.1, .3, 1)) - 1) / 5;
    color = texture(texture_sampler, samplePos) * shade;
    float fog = 1.0 - clamp(exp(-gl_FragCoord.z / gl_FragCoord.w * .001), 0.0, 1.0);
    color = mix(color, vec4(.5, .5, .5, 1.0), fog);
}