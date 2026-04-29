---
name: compare-with-main
description: Compare current implementation with the original logic in the 'main' branch by searching for semantic matches, ensuring functionality and edge cases are preserved during rewrites.
compatibility: opencode
---
## What I do
- **Identify Intent**: Determine the core logic or feature currently being worked on from the user's request.
- **Locate Original Logic**: 
    - Since file structures differ in rewrites, I will search the `main` branch for semantic matches using a multi-step discovery process:
        1. **File Discovery**: Use `git ls-tree -r main --name-only` to find files with relevant keywords.
        2. **Content Discovery**: Use `git grep` against the `main` branch to find specific functions or logic patterns.
        3. **Inspection**: Use `git show main:<path>` to inspect the source code of candidates found in `main`.
- **Deep Comparison**:
    - Once a candidate in `main` is found, I will compare:
        - **State/Actions**: Are all original state properties and action types accounted for?
        - **Reducers**: Does the new reducer handle the same state transitions and edge cases?
        - **Side Effects**: Are all original coroutines, subscriptions, and event listeners present?
- **Provide a structured report**:
    - ✅ **[MATCHED]**: Equivalent logic found.
    - 🔄 **[CHANGED]**: Behavior or implementation differs (with explanation of why).
    - ⚠️ **[MISSING]**: Logic or state from `main` that is absent in the current version.
    - 🚨 **[EDGE CASE RISK]**: Identified discrepancies that might cause regression.

## When to use me
Use this when you have just completed a refactor, rewrite, or significant feature implementation where the directory structure or filenames have changed compared to the `main` branch.
