import { mkdir, rm } from 'node:fs/promises';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { execFile } from 'node:child_process';
import { promisify } from 'node:util';

const execFileAsync = promisify(execFile);
const siteRoot = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const repoRoot = resolve(siteRoot, '..');
const toolSource = resolve(siteRoot, 'tools/ApiIndexExtractor.java');
const toolClasses = resolve(siteRoot, '.cache/api-tools');
const output = resolve(siteRoot, 'src/data/api-index.generated.json');

await rm(toolClasses, { recursive: true, force: true });
await mkdir(toolClasses, { recursive: true });

await execFileAsync('javac', ['-encoding', 'UTF-8', '-d', toolClasses, toolSource], {
  cwd: repoRoot,
  maxBuffer: 1024 * 1024 * 4
});

await execFileAsync(
  'java',
  ['-cp', toolClasses, 'ApiIndexExtractor', '--root', repoRoot, '--output', output],
  {
    cwd: repoRoot,
    maxBuffer: 1024 * 1024 * 16
  }
);
