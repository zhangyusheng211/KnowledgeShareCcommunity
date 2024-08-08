import * as THREE from '../../../utils/three/three.module.js';
// import Stats from '../../../utils/three/jsm/libs/stats.module.js';
// import { GUI } from '../../../utils/three/jsm/libs/lil-gui.module.min.js';
import { OrbitControls } from '../../../utils/three/jsm/controls/OrbitControls.js';
import { Water } from '../../../utils/three/jsm/objects/Water.js';
import { Sky } from '../../../utils/three/jsm/objects/Sky.js';


let container, stats;
let camera, scene, renderer;
let controls, water, sun;
let animationFrame;



export function init() {

    container = document.getElementById('calling-container');

    //

    renderer = new THREE.WebGLRenderer();
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.toneMapping = THREE.ACESFilmicToneMapping;
    container.appendChild(renderer.domElement);

    //

    scene = new THREE.Scene();

    camera = new THREE.PerspectiveCamera(55, window.innerWidth / window.innerHeight, 1, 20000);
    camera.position.set(30, 80, 100);

    //

    sun = new THREE.Vector3();

    // Water

    const waterGeometry = new THREE.PlaneGeometry(10000, 10000);

    water = new Water(
        waterGeometry,
        {
            textureWidth: 512,
            textureHeight: 512,
            waterNormals: new THREE.TextureLoader().load('http://qiniu.zwblog.net/waternormals.jpg', function (texture) {

                texture.wrapS = texture.wrapT = THREE.RepeatWrapping;

            }),
            sunDirection: new THREE.Vector3(),
            sunColor: 0xffffff,
            waterColor: 0x001e0f,
            distortionScale: 3.7,
            fog: scene.fog !== undefined
        }
    );

    water.rotation.x = - Math.PI / 2;

    scene.add(water);

    // Skybox

    const sky = new Sky();
    sky.scale.setScalar(10000);
    scene.add(sky);

    const skyUniforms = sky.material.uniforms;

    skyUniforms['turbidity'].value = 10;
    skyUniforms['rayleigh'].value = 2;
    skyUniforms['mieCoefficient'].value = 0.005;
    skyUniforms['mieDirectionalG'].value = 0.8;

    const parameters = {
        elevation: 22.5,
        azimuth: 180
    };

    const pmremGenerator = new THREE.PMREMGenerator(renderer);

    function updateSun() {

        const phi = THREE.MathUtils.degToRad(90 - parameters.elevation);
        const theta = THREE.MathUtils.degToRad(parameters.azimuth);

        sun.setFromSphericalCoords(1, phi, theta);

        sky.material.uniforms['sunPosition'].value.copy(sun);
        water.material.uniforms['sunDirection'].value.copy(sun).normalize();

        scene.environment = pmremGenerator.fromScene(sky).texture;

    }

    updateSun();
    //

    controls = new OrbitControls(camera, renderer.domElement);
    controls.maxPolarAngle = Math.PI * 0.495;
    controls.target.set(0, 10, 0);
    controls.minDistance = 40.0;
    controls.maxDistance = 200.0;

    // 鼠标右键拖拽
    controls.enablePan = false;
    // 缩放
    controls.enableZoom = false;
    // 禁用拖拽
    controls.enableRotate = false;

    controls.update();

    //

    // stats = new Stats();
    // container.appendChild(stats.dom);

    // GUI

    // const gui = new GUI();

    // const folderSky = gui.addFolder('Sky');
    // folderSky.add(parameters, 'elevation', 0, 90, 0.1).onChange(updateSun);
    // folderSky.add(parameters, 'azimuth', - 180, 180, 0.1).onChange(updateSun);
    // folderSky.open();

    // const waterUniforms = water.material.uniforms;

    // const folderWater = gui.addFolder('Water');
    // folderWater.add(waterUniforms.distortionScale, 'value', 0, 8, 0.1).name('distortionScale');
    // folderWater.add(waterUniforms.size, 'value', 0.1, 10, 0.1).name('size');
    // folderWater.open();

    //

    window.addEventListener('resize', onWindowResize);

}

function onWindowResize() {

    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();

    renderer.setSize(window.innerWidth, window.innerHeight);

}

export function animate() {

    // animationFrame = requestAnimationFrame(animate);
    // render();
    // stats.update();

    requestAnimationFrame(() => {
        render();
        animate();
    })

}

function render() {
    if (water) {
        water.material.uniforms['time'].value += 0.8 / 60.0;
        renderer.render(scene, camera);
    }
}

export function clearAll() {
    controls = null;
    water = null;
    sun = null;
    renderer.clear();
    scene.clear();
    camera.clear();
    window.cancelAnimationFrame(animationFrame);
}
