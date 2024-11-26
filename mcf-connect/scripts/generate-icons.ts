// scripts/generate-icons.ts
import sharp from 'sharp'
import fs from 'fs'
import path from 'path'

const sizes = [16, 32, 48, 64, 128]
const sourcePath = path.join(__dirname, '../src/assets/icons/MCFC.png')
const outputDir = path.join(__dirname, '../assets')

async function generateIcons() {
    for (const size of sizes) {
        await sharp(sourcePath)
            .resize(size, size)
            .toFile(path.join(outputDir, `icon${size}.png`))
    }
}

generateIcons()