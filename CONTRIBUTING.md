# Contributing to No More Recipe Conflicts Again

## Branch Strategy

This project follows the GitFlow branching model:

### Main Branches

- **`main`** (or `master`) - Production-ready code. Only stable releases are merged here.
- **`develop`** - Integration branch for features. All feature branches merge here first.

### Supporting Branches

- **Feature branches** (`feature/*`)
  - Created from: `develop`
  - Merge back into: `develop`
  - Naming: `feature/description-of-feature`
  - Example: `feature/crafting-gui`, `feature/mod-integration`

- **Release branches** (`release/*`)
  - Created from: `develop`
  - Merge back into: `main` and `develop`
  - Naming: `release/version-number`
  - Example: `release/1.0.0`

- **Hotfix branches** (`hotfix/*`)
  - Created from: `main`
  - Merge back into: `main` and `develop`
  - Naming: `hotfix/description`
  - Example: `hotfix/crash-fix`

## Workflow

1. **Starting new feature development:**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/your-feature-name
   ```

2. **Completing a feature:**
   ```bash
   git checkout develop
   git merge --no-ff feature/your-feature-name
   git push origin develop
   git branch -d feature/your-feature-name
   ```

3. **Creating a release:**
   ```bash
   git checkout -b release/1.0.0 develop
   # Update version numbers, test thoroughly
   git checkout main
   git merge --no-ff release/1.0.0
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git checkout develop
   git merge --no-ff release/1.0.0
   ```

## Commit Messages

Follow conventional commit format:
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `style:` Code style changes (formatting, etc.)
- `refactor:` Code refactoring
- `test:` Test additions or changes
- `chore:` Build process or auxiliary tool changes

Example: `feat: add GUI for recipe selection`

## Code Standards

- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Test your changes thoroughly
- Ensure compatibility with listed Minecraft/NeoForge versions

## Pull Request Process

1. Update the README.md with details of changes if needed
2. Ensure all tests pass
3. Request review from maintainers
4. PR will be merged after approval

## Development Setup

1. Clone the repository
2. Import as Gradle project in your IDE
3. Run `./gradlew build` to verify setup
4. Use `./gradlew runClient` for testing