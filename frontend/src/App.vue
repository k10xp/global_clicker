<template>
  <main
    class="relative grid min-h-svh place-items-center overflow-hidden bg-linear-to-br from-bg-dark to-bg-darker"
  >
    <!--
      No transform/filter/isolation on this stage — those create a stacking
      context and mix-blend-mode can only see siblings inside it (not the
      page gradient), so color-blend glows look invisible.
    -->
    <div
      id="clicker-stage"
      ref="stageEl"
      class="relative h-[min(70vh,36rem)] w-[min(92vw,58rem)]"
    >
      <h1
        class="font-jersey absolute -top-10 -left-10 z-20 flex flex-col gap-4 leading-none uppercase"
      >
        <span
          class="block text-[65px] tracking-[0.4em] [text-box:trim-both_cap_alphabetic]"
        >
          Global
        </span>
        <span
          class="block text-[110px] tracking-wide [text-box:trim-both_cap_alphabetic]"
        >
          Clicker
        </span>
      </h1>

      <div
        class="pointer-events-none absolute inset-0 size-full text-green-dim select-none [&_svg]:size-full"
        aria-hidden="true"
        v-html="globalMapSvg"
      />

      <div
        v-for="loc in locations"
        :key="`glow-${loc.id}`"
        class="pointer-events-none absolute size-36 -mt-18 -ml-18 rounded-full mix-blend-color transition-opacity duration-300 sm:size-48 sm:-mt-24 sm:-ml-24"
        :class="isAwake(loc) ? 'opacity-100' : 'opacity-0'"
        :style="{
          left: `${loc.target.x}%`,
          top: `${loc.target.y}%`,
          background: `radial-gradient(circle, ${cssColor[loc.color]} 0%, ${cssColor[loc.color]}00 68%)`,
        }"
        aria-hidden="true"
      />

      <svg
        class="pointer-events-none absolute inset-0 z-[1] size-full overflow-visible"
        viewBox="0 0 100 100"
        preserveAspectRatio="none"
        aria-hidden="true"
      >
        <line
          v-for="loc in locations"
          :key="`line-${loc.id}`"
          v-bind="linePoints(loc)"
          class="stroke-current transition-[color,opacity] duration-300"
          :class="[toneClass(loc), fadeClass(loc)]"
          stroke-width="4.5"
          stroke-linecap="round"
          vector-effect="non-scaling-stroke"
        />
      </svg>

      <div
        v-for="loc in locations"
        :key="`target-${loc.id}`"
        class="absolute z-[1] size-10 -mt-5 -ml-5 cursor-pointer transition-[color,opacity] duration-300 sm:size-12 sm:-mt-6 sm:-ml-6"
        :class="[toneClass(loc), fadeClass(loc)]"
        :style="{
          left: `${loc.target.x}%`,
          top: `${loc.target.y}%`,
        }"
        @mouseenter="wake(loc.id)"
        @mouseleave="sleep()"
        @click="toggleLit(loc.id)"
      >
        <span
          aria-hidden="true"
          class="absolute inset-0 transition-opacity duration-300"
          :class="isAwake(loc) ? 'opacity-45' : 'opacity-0'"
          style="filter: blur(5px)"
        >
          <span class="absolute inset-[18%] rounded-full border-2 border-current" />
          <span
            class="absolute top-1/2 left-1/2 size-1.5 -translate-x-1/2 -translate-y-1/2 rounded-full bg-current sm:size-2"
          />
          <span
            class="absolute top-[2%] left-1/2 h-[14%] w-0.5 -translate-x-1/2 bg-current"
          />
          <span
            class="absolute bottom-[2%] left-1/2 h-[14%] w-0.5 -translate-x-1/2 bg-current"
          />
          <span
            class="absolute top-1/2 left-[2%] h-0.5 w-[14%] -translate-y-1/2 bg-current"
          />
          <span
            class="absolute top-1/2 right-[2%] h-0.5 w-[14%] -translate-y-1/2 bg-current"
          />
        </span>
        <span
          aria-hidden="true"
          class="absolute inset-0 transition-opacity duration-300"
          :class="isAwake(loc) ? 'opacity-30' : 'opacity-0'"
          style="filter: blur(12px)"
        >
          <span class="absolute inset-[18%] rounded-full border-2 border-current" />
          <span
            class="absolute top-1/2 left-1/2 size-1.5 -translate-x-1/2 -translate-y-1/2 rounded-full bg-current sm:size-2"
          />
          <span
            class="absolute top-[2%] left-1/2 h-[14%] w-0.5 -translate-x-1/2 bg-current"
          />
          <span
            class="absolute bottom-[2%] left-1/2 h-[14%] w-0.5 -translate-x-1/2 bg-current"
          />
          <span
            class="absolute top-1/2 left-[2%] h-0.5 w-[14%] -translate-y-1/2 bg-current"
          />
          <span
            class="absolute top-1/2 right-[2%] h-0.5 w-[14%] -translate-y-1/2 bg-current"
          />
        </span>

        <div class="relative size-full">
          <span
            class="absolute inset-[18%] rounded-full border-2 border-current"
          />
          <span
            class="absolute top-1/2 left-1/2 size-1.5 -translate-x-1/2 -translate-y-1/2 rounded-full bg-current sm:size-2"
          />
          <span
            class="absolute top-[2%] left-1/2 h-[14%] w-0.5 -translate-x-1/2 bg-current"
          />
          <span
            class="absolute bottom-[2%] left-1/2 h-[14%] w-0.5 -translate-x-1/2 bg-current"
          />
          <span
            class="absolute top-1/2 left-[2%] h-0.5 w-[14%] -translate-y-1/2 bg-current"
          />
          <span
            class="absolute top-1/2 right-[2%] h-0.5 w-[14%] -translate-y-1/2 bg-current"
          />
        </div>
      </div>

      <button
        v-for="loc in locations"
        :key="`pod-${loc.id}`"
        :ref="(el) => setPodRef(loc.id, el)"
        type="button"
        class="absolute z-10 cursor-pointer bg-transparent p-0 text-left transition-[color,opacity] duration-300 focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-current"
        :class="[toneClass(loc), fadeClass(loc)]"
        :style="{
          left: `${loc.pod.x}%`,
          top: `${loc.pod.y}%`,
          transform: 'translate(-50%, -50%)',
        }"
        @mouseenter="wake(loc.id)"
        @mouseleave="sleep()"
        @focus="wake(loc.id)"
        @blur="sleep()"
        @click="toggleLit(loc.id)"
      >
        <span
          aria-hidden="true"
          class="pointer-events-none absolute inset-0 -z-10 transition-opacity duration-300"
          :class="isAwake(loc) ? 'opacity-40' : 'opacity-0'"
          style="filter: blur(10px)"
        >
          <span class="absolute inset-0 bg-current" :class="loc.corners" />
        </span>
        <span
          aria-hidden="true"
          class="pointer-events-none absolute inset-0 -z-10 transition-opacity duration-300"
          :class="isAwake(loc) ? 'opacity-25' : 'opacity-0'"
          style="filter: blur(22px)"
        >
          <span class="absolute inset-0 bg-current" :class="loc.corners" />
        </span>

        <span
          class="relative block bg-current p-[5px] transition active:brightness-95"
          :class="loc.corners"
        >
          <span
            class="block bg-bg-darker px-5 py-3.5 sm:px-6 sm:py-4"
            :class="loc.corners"
          >
            <span class="font-jersey block text-3xl leading-none sm:text-4xl">
              {{ loc.country }}
            </span>
            <span
              class="font-ubuntu-mono block text-xs tracking-wide uppercase sm:text-sm"
            >
              {{ loc.city }} • {{ clickCounts[loc.id] }} clicks
            </span>
          </span>
        </span>
      </button>
    </div>
  </main>
</template>

<script setup>
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import globalMapSvg from './assets/global.svg?raw'

const stageEl = ref(null)
const podEls = {}
const pads = reactive({})
const LINE_STROKE = 4.5

const hoveredId = ref(null)
const lit = reactive({})
const clickCounts = reactive({
  us: 0,
  se: 0,
  de: 0,
})
const litTimers = {}
let sleepTimer
let resizeObserver

function setPodRef(id, el) {
  if (el) {
    podEls[id] = el
    resizeObserver?.observe(el)
  } else if (podEls[id]) {
    resizeObserver?.unobserve(podEls[id])
    delete podEls[id]
  }
  measurePads()
}

function measurePads() {
  const stage = stageEl.value
  if (!stage) return
  const { width: sw, height: sh } = stage.getBoundingClientRect()
  if (!sw || !sh) return

  for (const loc of locations) {
    const el = podEls[loc.id]
    if (!el) continue
    const { width, height } = el.getBoundingClientRect()
    pads[loc.id] = {
      x: (width / 2 / sw) * 100,
      y: (height / 2 / sh) * 100,
    }
  }
}

function wake(id) {
  clearTimeout(sleepTimer)
  hoveredId.value = id
}

function sleep() {
  clearTimeout(sleepTimer)
  sleepTimer = setTimeout(() => {
    hoveredId.value = null
  }, 40)
}

function toggleLit(id) {
  clickCounts[id] += 1
  lit[id] = true
  clearTimeout(litTimers[id])
  litTimers[id] = setTimeout(() => {
    lit[id] = false
  }, 5000)
}

function isAwake(loc) {
  return hoveredId.value === loc.id || !!lit[loc.id]
}

/** Sleep: gray + 50% opacity on the whole pod (border, fill, text). */
function toneClass(loc) {
  return isAwake(loc) ? colorClass[loc.color] : 'text-sleep'
}

function fadeClass(loc) {
  return isAwake(loc) ? 'opacity-100' : 'opacity-50'
}

const locations = [
  {
    id: 'us',
    country: 'United States',
    city: 'Los Angeles',
    color: 'green',
    corners: 'pixel-corners-a',
    target: { x: 24, y: 40 },
    pod: { x: 14, y: 64 },
  },
  {
    id: 'se',
    country: 'Sweden',
    city: 'Stockholm',
    color: 'blue',
    corners: 'pixel-corners-b',
    target: { x: 56, y: 27 },
    pod: { x: 74, y: 12 },
  },
  {
    id: 'de',
    country: 'Germany',
    city: 'Berlin',
    color: 'pink',
    corners: 'pixel-corners-c',
    target: { x: 54, y: 37 },
    pod: { x: 71, y: 55 },
  },
]

/**
 * Attach at the pod's outer border (AABB exit toward the target), end at
 * crosshair. Pull back half the stroke so the round cap sits on the edge.
 */
function linePoints(loc) {
  const { pod, target } = loc
  const pad = pads[loc.id]
  if (!pad) {
    return { x1: pod.x, y1: pod.y, x2: target.x, y2: target.y }
  }

  const dx = target.x - pod.x
  const dy = target.y - pod.y
  const len = Math.hypot(dx, dy) || 1
  const ux = dx / len
  const uy = dy / len
  const tx = pad.x / Math.max(Math.abs(ux), 1e-6)
  const ty = pad.y / Math.max(Math.abs(uy), 1e-6)
  let t = Math.min(tx, ty)

  const stage = stageEl.value
  if (stage) {
    const sw = stage.clientWidth
    const sh = stage.clientHeight
    const px = (ux / 100) * sw
    const py = (uy / 100) * sh
    const plen = Math.hypot(px, py) || 1
    t -= (LINE_STROKE / 2) / plen
  }

  return {
    x1: pod.x + ux * t,
    y1: pod.y + uy * t,
    x2: target.x,
    y2: target.y,
  }
}

onMounted(() => {
  resizeObserver = new ResizeObserver(measurePads)
  if (stageEl.value) resizeObserver.observe(stageEl.value)
  for (const el of Object.values(podEls)) resizeObserver.observe(el)
  measurePads()
})

onUnmounted(() => {
  resizeObserver?.disconnect()
})

const colorClass = {
  green: 'text-green',
  blue: 'text-blue',
  pink: 'text-pink',
}

const cssColor = {
  green: '#20d3ee',
  blue: '#4d8cff',
  pink: '#a78bfa',
}
</script>
