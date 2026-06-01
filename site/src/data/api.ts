import apiItemsJson from './api-index.generated.json';

export type ApiMember = {
  name: string;
  kind: 'constructor' | 'method' | 'field' | 'nested-type';
  signature: string;
  status: 'stable' | 'deprecated';
  summary: string;
};

export type ApiItem = {
  name: string;
  qualifiedName: string;
  module: string;
  artifact: string;
  packageName: string;
  type: 'class' | 'interface' | 'annotation' | 'enum' | 'record';
  status: 'stable' | 'deprecated';
  summary: string;
  docsPath: string;
  sourcePath: string;
  since: string;
  deprecatedMessage: string;
  members: ApiMember[];
};

export type ApiIndex = {
  meta: {
    generatedBy: string;
    source: string;
    sourceFileCount: number;
    artifactCount: number;
    typeCount: number;
    memberCount: number;
    artifacts: Array<{
      artifact: string;
      module: string;
      typeCount: number;
    }>;
  };
  items: ApiItem[];
};

export const apiIndex = apiItemsJson as ApiIndex;
export const apiItems = apiIndex.items;
export const apiMeta = apiIndex.meta;

export function apiAnchor(item: ApiItem) {
  return item.qualifiedName.replaceAll('.', '-');
}
