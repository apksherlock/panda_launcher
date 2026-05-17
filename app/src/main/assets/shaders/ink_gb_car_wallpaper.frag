// PandaLauncher — ambient LCD wave band (GLSL ES 2.0)
//
// Uniforms:
//   resolution, baseColor, accentColor
//   patternScale  — wave coarseness / band size (~0.2–0.5)
//   scanlineOpacity — LCD scanlines (keep low)

#version 100

precision mediump float;

uniform vec2 resolution;
uniform vec4 baseColor;
uniform vec4 accentColor;
uniform float patternScale;
uniform float scanlineOpacity;

const float AMBIENT_INK = 0.11;

void main() {
    vec2 res = max(resolution, vec2(1.0));
    vec2 uv = gl_FragCoord.xy / res;

    vec2 p = uv - vec2(0.5, 0.52);
    p.x *= res.x / res.y;

    float bandHalf = mix(0.09, 0.16, clamp(patternScale, 0.15, 0.55));
    float inBand = step(abs(p.y), bandHalf);

    float cell = mix(5.0, 9.0, clamp(patternScale, 0.15, 0.55));
    vec2 g = floor(gl_FragCoord.xy / cell);

    // Staggered horizontal wave lines (column shift via mod + step)
    float shift = floor(mod(g.x, 6.0) * 0.5);
    float row = mod(g.y + shift, 5.0);
    float line = step(3.2, row);

    float shift2 = floor(mod(g.x + 3.0, 6.0) * 0.5);
    float row2 = mod(g.y + shift2 + 1.0, 5.0);
    float line2 = step(3.5, row2) * 0.5;

    float ink = max(line, line2) * inBand * AMBIENT_INK;

    vec4 color = mix(baseColor, accentColor, ink);

    float rowScan = mod(floor(gl_FragCoord.y), 2.0);
    float scanMask = step(0.5, rowScan);
    color.rgb *= mix(1.0, 0.94, scanlineOpacity * scanMask);

    gl_FragColor = color;
}
