# Tilt Angle Limitation: Why Bubble Stays on One Side

## The Problem

When changing X-rotation in the emulator, the Tilt Y value never reaches ±180° and the bubble seems stuck on one side of the radar, depending on the direction you slide.

## Root Cause

The tilt angle is calculated using this formula (`LevelSensorManager.kt:89`):

```kotlin
val angleY = atan2(
    filteredY.toDouble(),
    sqrt((filteredX * filteredX + filteredZ * filteredZ).toDouble())
)
```

The denominator `sqrt(ax² + az²)` is **always >= 0**, so `atan2` can only return values in the range **[-90°, +90°]**. It can never reach ±180°.

## What Happens as X-rot Goes from 0° to 180°

| Emulator X-rot | accelY | accelZ | angleY (Tilt Y) | Bubble |
|----------------|--------|--------|------------------|--------|
| 0°             | 0      | +9.8   | 0°               | center |
| 45°            | +6.9   | +6.9   | +45°             | moves UP |
| 90°            | +9.8   | 0      | +90°             | at TOP (max) |
| 135°           | +6.9   | -6.9   | +45° (folds back!) | back toward center |
| 180°           | 0      | -9.8   | 0°               | center again |

The angle increases to 90° then **folds back** toward 0° instead of continuing to 180°. The formula cannot distinguish between 45° and 135° tilt.

## Why It Seems Stuck on the Bottom

- Sliding X-rot in the **negative** direction (0 → -180): angleY is negative → bubble only goes **DOWN**
- Sliding X-rot in the **positive** direction (0 → +180): angleY is positive → bubble only goes **UP**

The bubble does reach both sides, but only depending on the sign of X-rot.

## Why This Happens

This is a fundamental limitation of deriving tilt from a single accelerometer using `atan2(component, magnitude_of_others)`. Once the device is tilted past 90° (approaching upside-down), the accelerometer alone cannot determine which "side" of vertical the device is on — the Y-component of gravity starts decreasing in both cases.

To get full 360° rotation tracking, you would need to use a different sensor such as `TYPE_ROTATION_VECTOR` or combine the accelerometer with a gyroscope (sensor fusion).
