import docsIndexJson from './docs-index.generated.json';

export type GeneratedDocEntry = {
  slug: string;
  category: 'overview' | 'guide' | 'release' | 'module';
  artifact: string;
  moduleSlug: string;
  title: string;
  summary: string;
  source: string;
  href: string;
  headings: Array<{
    depth: number;
    title: string;
    id: string;
  }>;
  codeBlockCount: number;
  markdown: string;
};

export type GeneratedDocsIndex = {
  meta: {
    generatedBy: string;
    source: string;
    entryCount: number;
  };
  entries: GeneratedDocEntry[];
};

export const docsIndex = docsIndexJson as GeneratedDocsIndex;
export const generatedDocEntries = docsIndex.entries;
export const overviewDoc = generatedDocEntries.find((entry) => entry.category === 'overview');
export const guideDocs = generatedDocEntries.filter((entry) => entry.category === 'guide');
export const releaseDocs = generatedDocEntries.filter((entry) => entry.category === 'release');
export const moduleDocs = generatedDocEntries.filter((entry) => entry.category === 'module');

export function getModuleReadme(moduleSlug: string) {
  return moduleDocs.find((entry) => entry.moduleSlug === moduleSlug);
}
