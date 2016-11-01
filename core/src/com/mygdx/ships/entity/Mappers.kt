package com.mygdx.ships.entity

import com.badlogic.ashley.core.ComponentMapper
import com.mygdx.ships.entity.components.*

object Mappers {
    val bodyComponent = ComponentMapper.getFor(BodyComponent::class.java)
    val bombComponent = ComponentMapper.getFor(BombComponent::class.java)
    val cameraComponent = ComponentMapper.getFor(CameraComponent::class.java)
    val colorComponent = ComponentMapper.getFor(ColorComponent::class.java)
    val expandComponent = ComponentMapper.getFor(ExpandComponent::class.java)
    val particleEmitter = ComponentMapper.getFor(ParticleEmitter::class.java)
    val playerMovement = ComponentMapper.getFor(PlayerMovement::class.java)
    val explosionComponent = ComponentMapper.getFor(ExplosionComponent::class.java)
    val fadeComponent = ComponentMapper.getFor(FadeComponent::class.java)
    val followerComponent = ComponentMapper.getFor(FollowerComponent::class.java)
    val leaderComponent = ComponentMapper.getFor(LeaderComponent::class.java)
    val limitedLifeSpan = ComponentMapper.getFor(LimitedLifespan::class.java)
    val lineComponent = ComponentMapper.getFor(LineComponent::class.java)
    val polygonComponent = ComponentMapper.getFor(PolygonComponent::class.java)
    val sapComponent = ComponentMapper.getFor(SapComponent::class.java)
    val scoreComponent = ComponentMapper.getFor(ScoreComponent::class.java)
    val transform = ComponentMapper.getFor(Transform::class.java)
    val terrainComponent = ComponentMapper.getFor(TerrainComponent::class.java)
    val tetherComponent = ComponentMapper.getFor(TetherComponent::class.java)
    val textureComponent = ComponentMapper.getFor(TextureComponent::class.java)
    val weaponComponent = ComponentMapper.getFor(WeaponComponent::class.java)
    val weaponAnimation = ComponentMapper.getFor(WeaponAnimation::class.java)

}