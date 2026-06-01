import { apiAnchor, apiItems } from './api';
import { docLinks } from './docs';
import { generatedDocEntries } from './generatedDocs';
import { modules } from './modules';

export type SearchEntry = {
  title: string;
  kind: '文档' | '模块' | 'API';
  href: string;
  text: string;
};

export const searchEntries: SearchEntry[] = [
  ...generatedDocEntries.map((doc) => ({
    title: doc.title,
    kind: '文档' as const,
    href: doc.href,
    text: `${doc.title} ${doc.summary} ${doc.source} ${doc.headings.map((heading) => heading.title).join(' ')}`
  })),
  ...docLinks.map((doc) => ({
    title: doc.title,
    kind: '文档' as const,
    href: doc.href,
    text: `${doc.title} ${doc.description} ${doc.source}`
  })),
  ...modules.map((module) => ({
    title: module.artifact,
    kind: '模块' as const,
    href: `/docs/modules/${module.slug}/`,
    text: `${module.title} ${module.artifact} ${module.summary} ${module.highlights.join(' ')}`
  })),
  ...apiItems.map((item) => ({
    title: item.name,
    kind: 'API' as const,
    href: `/api/#${apiAnchor(item)}`,
    text: `${item.name} ${item.qualifiedName} ${item.artifact} ${item.module} ${item.packageName} ${item.type} ${item.status} ${item.summary} ${item.members.map((member) => `${member.name} ${member.signature} ${member.summary}`).join(' ')}`
  }))
].filter((entry, index, entries) => entries.findIndex((candidate) => candidate.href === entry.href && candidate.title === entry.title) === index);
